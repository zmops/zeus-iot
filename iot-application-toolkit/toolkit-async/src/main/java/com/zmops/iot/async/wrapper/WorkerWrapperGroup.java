package com.zmops.iot.async.wrapper;

import com.zmops.iot.async.executor.PollingCenter;
import com.zmops.iot.async.executor.timer.SystemClock;
import com.zmops.iot.async.util.timer.Timeout;
import com.zmops.iot.async.util.timer.TimerTask;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * @author create by TcSnZh on 2021/5/9-下午7:21
 */
public class WorkerWrapperGroup {
    /**
     * 任务开始时间
     */
    private final    long                             groupStartTime;
    /**
     * 任务限时
     */
    private final    long                             timeoutLength;
    /**
     * 该map存放所有wrapper的id和wrapper映射
     * <p/>
     * 需要线程安全。
     */
    private final    Map<String, WorkerWrapper<?, ?>> forParamUseWrappers = new ConcurrentHashMap<>();
    /**
     * 当全部wrapper都调用结束，它会countDown
     */
    private final    CountDownLatch                   endCDL              = new CountDownLatch(1);
    /**
     * 检测到超时，此标记变量将为true。
     */
    private final    AtomicBoolean                    anyTimeout          = new AtomicBoolean(false);
    /**
     * 结束时间
     */
    private volatile long                             finishTime          = -1L;
    /**
     * 取消任务状态
     * 0 - not cancel , 1 - waiting cancel , 2 - already cancel
     */
    private final    AtomicInteger                    cancelState         = new AtomicInteger();

    public static final int NOT_CANCEL     = 0;
    public static final int WAITING_CANCEL = 1;
    public static final int ALREADY_CANCEL = 2;

    public WorkerWrapperGroup(long groupStartTime, long timeoutLength) {
        this.groupStartTime = groupStartTime;
        this.timeoutLength = timeoutLength;
    }

    public void addWrapper(Collection<? extends WorkerWrapper<?, ?>> wrapper) {
        Objects.requireNonNull(wrapper).forEach(this::addWrapper);
    }

    public void addWrapper(WorkerWrapper<?, ?>... wrappers) {
        for (WorkerWrapper<?, ?> wrapper : Objects.requireNonNull(wrappers)) {
            addWrapper(wrapper);
        }
    }

    public void addWrapper(WorkerWrapper<?, ?> wrapper) {
        if (wrapper != null) {
            forParamUseWrappers.put(wrapper.id, wrapper);
        }
    }

    public Map<String, WorkerWrapper<?, ?>> getForParamUseWrappers() {
        return forParamUseWrappers;
    }

    public CountDownLatch getEndCDL() {
        return endCDL;
    }

    public long getGroupStartTime() {
        return groupStartTime;
    }

    public AtomicBoolean getAnyTimeout() {
        return anyTimeout;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public boolean isCancelled() {
        return cancelState.get() == ALREADY_CANCEL;
    }

    public boolean isWaitingCancel() {
        return cancelState.get() == WAITING_CANCEL;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean pleaseCancel() {
        return cancelState.compareAndSet(NOT_CANCEL, WAITING_CANCEL);
    }

    public class CheckFinishTask implements TimerTask {

        @SuppressWarnings("RedundantThrows")
        @Override
        public void run(Timeout timeout) throws Exception {
            // 已经完成了
            if (endCDL.getCount() < 1) {
                return;
            }
            AtomicBoolean hasTimeout = new AtomicBoolean(false);
            // 记录正在运行中的wrapper里，最近的限时时间。
            final AtomicLong                      minDaley   = new AtomicLong(Long.MAX_VALUE);
            final Collection<WorkerWrapper<?, ?>> values     = forParamUseWrappers.values();
            final Stream<WorkerWrapper<?, ?>>     stream     = values.size() > 128 ? values.parallelStream() : values.stream();
            final boolean                         needCancel = cancelState.get() == WAITING_CANCEL;
            boolean allFinish_and_notNeedCancel = stream
                    // 需要取消的话就取消
                    .peek(wrapper -> {
                        if (needCancel) {
                            wrapper.cancel();
                        }
                    })
                    // 检查超时并保存最近一次限时时间
                    // 当需要取消时，才会不断遍历。如果不需要取消，则计算一次(或并行流中多次)就因allMatch不满足而退出了。
                    .peek(wrapper -> {
                        // time_diff :
                        // -1  ->  already timeout ;
                        // 0   ->  finish but not timeout ;
                        // X>0 ->  is running , may timeout in X seconds .
                        long time_diff = wrapper.checkTimeout(true, groupStartTime, timeoutLength);
                        if (time_diff < 0) {
                            hasTimeout.set(true);
                        }
                        if (time_diff == 0) {
                            return;
                        }
                        // use CAS and SPIN for thread safety in parallelStream .
                        do {
                            long getMinDaley = minDaley.get();
                            // 需要设置最小时间，但是cas失败，则自旋
                            if (getMinDaley <= time_diff && !minDaley.compareAndSet(getMinDaley, time_diff)) {
                                continue;
                            }
                            return;
                        } while (true);
                    })
                    // 判断是否不需要取消且全部结束
                    // 在不需要取消时，这里如果还有未结束的wrapper则会提前结束流并返回false
                    // 在需要取消时，会全部遍历一遍并取消掉已经进入链路的wrapper
                    .allMatch(wrapper -> !needCancel && wrapper.getState().finished());
            long getMinDaley = minDaley.get();
            // 如果本次取消掉了任务，或是所有wrapper都已经完成
            // ( ps : 前后两条件在这里是必定 一真一假 或 两者全假 )
            if (needCancel || allFinish_and_notNeedCancel) {
                // 如果这次进行了取消，则设置取消状态为已完成
                if (needCancel) {
                    cancelState.set(ALREADY_CANCEL);
                }
                anyTimeout.set(hasTimeout.get());
                finishTime = SystemClock.now();
                endCDL.countDown();
            }
            // 如果有正在运行的wrapper
            else {
                // 如果有正在WORKING的wrapper，则计算一下限时时间，限时完成后轮询它。
                if (getMinDaley != Long.MAX_VALUE) {
                    PollingCenter.getInstance().checkGroup(this, getMinDaley);
                }
            }
        }

        // hashCode and equals will called WorkerWrapperGroup.this

        /**
         * 将会调用{@link WorkerWrapperGroup#hashCode()}
         */
        @Override
        public int hashCode() {
            return WorkerWrapperGroup.this.hashCode();
        }

        /**
         * 将会调用{@link WorkerWrapperGroup#equals(Object)}
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof CheckFinishTask)) {
                return false;
            }
            return Objects.equals(WorkerWrapperGroup.this, ((CheckFinishTask) obj).getParent());
        }

        private WorkerWrapperGroup getParent() {
            return WorkerWrapperGroup.this;
        }
    }
}
