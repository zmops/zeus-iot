package com.zmops.iot.async.worker;

import com.zmops.iot.async.executor.timer.SystemClock;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.async.wrapper.WorkerWrapperGroup;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 一次工作结果的总接口。
 *
 * @author tcsnzh[zh.jobs@foxmail.com] create this in 2021/5/25-下午3:22
 */
public interface OnceWork {
    /**
     * 返回唯一的workId
     */
    String workId();

    /**
     * 判断是否结束。因超时而结束也算结束。
     */
    boolean isFinish();

    /**
     * 同步等待到结束。
     */
    void awaitFinish() throws InterruptedException;

    /**
     * 判断是否超时
     *
     * @return 如果尚未结束或已结束但未超时，返回false。已结束且已经超时返回true。
     */
    boolean hasTimeout();

    /**
     * 判断是否全部wrapper都处于 执行成功 或 跳过。
     *
     * @return 如果已经结束，所有wrapper都成功或跳过返回true，否则返回false。如果尚未结束，返回false。
     */
    default boolean allSuccess() {
        if (!isFinish()) {
            return false;
        }
        return getWrappers().values().stream().allMatch(wrapper -> {
            final ResultState state = wrapper.getWorkResult().getResultState();
            return state == ResultState.SUCCESS || state == ResultState.DEFAULT;
        });
    }

    /**
     * 获取全部参与到工作中的wrapper。
     */
    Map<String, WorkerWrapper<?, ?>> getWrappers();

    /**
     * 获取{@link WorkResult#getResultState()}为{@link ResultState#SUCCESS}的wrapper。
     */
    default Map<String, WorkerWrapper<?, ?>> getSuccessWrappers() {
        return getWrappersOfState(ResultState.SUCCESS);
    }

    /**
     * 获取状态于这些state中的wrapper。
     *
     * @param ofState 状态列表
     * @return 返回Map
     */
    default Map<String, WorkerWrapper<?, ?>> getWrappersOfState(ResultState... ofState) {
        final HashSet<ResultState> states = new HashSet<>(Arrays.asList(ofState));
        if (states.isEmpty()) {
            return new HashMap<>(1);
        }
        return getWrappers().entrySet().stream()
                .filter(entry -> states.contains(entry.getValue().getWorkResult().getResultState()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 获取启动时间
     */
    long getStartTime();

    /**
     * 获取结束时间
     *
     * @return 如果超时，返回超时的时刻。如果尚未结束，则抛出异常。
     * @throws IllegalStateException 尚未结束，抛出异常。
     */
    long getFinishTime();

    /**
     * @return 已经取消完成
     */
    boolean isCancelled();

    /**
     * @return 是否正在取消中
     */
    boolean isWaitingCancel();

    /**
     * 请求异步取消。
     */
    void pleaseCancel();

    /**
     * 同步等待取消完成。
     */
    default void pleaseCancelAndAwaitFinish() throws InterruptedException {
        if (!isCancelled() && !isWaitingCancel()) {
            pleaseCancel();
        }
        awaitFinish();
    }

    /**
     * @return 返回 {@link AsFuture}封装对象。
     */
    default AsFuture asFuture() {
        return new AsFuture(this, limitTime -> limitTime / 16);
    }

    /**
     * 返回{@link Future}视图。
     *
     * @param sleepCheckInterval 为防止线程爆炸，在{@link Future#get(long, TimeUnit)}方法时使用隔一段时间检查一次。
     *                           该Function的参数为总超时毫秒值，返回值为检查时间间隔。
     * @return 返回 {@link AsFuture}封装对象。
     */
    default AsFuture asFuture(Function<Long, Long> sleepCheckInterval) {
        return new AsFuture(this, sleepCheckInterval);
    }

    // static

    /**
     * 空任务
     */
    static OnceWork emptyWork(String workId) {
        return new EmptyWork(workId);
    }

    // class

    class AsFuture implements Future<Map<String, WorkerWrapper<?, ?>>> {
        private final OnceWork             onceWork;
        private final Function<Long, Long> sleepCheckInterval;

        private AsFuture(OnceWork onceWork, Function<Long, Long> sleepCheckInterval) {
            this.onceWork = onceWork;
            this.sleepCheckInterval = sleepCheckInterval;
        }

        /**
         * 同步等待取消。
         *
         * @param ignore 该参数将被无视。因为暂未实现“修改允许打断属性”功能。todo : await implement
         */
        @Override
        public boolean cancel(boolean ignore) {
            try {
                if (onceWork.isFinish()) {
                    return false;
                }
                onceWork.pleaseCancelAndAwaitFinish();
            } catch (InterruptedException e) {
                throw new RuntimeException("interrupted when await finish in : " + this, e);
            }
            return true;
        }

        @Override
        public boolean isCancelled() {
            return onceWork.isCancelled();
        }

        @Override
        public boolean isDone() {
            return onceWork.isFinish();
        }

        @Override
        public Map<String, WorkerWrapper<?, ?>> get() throws InterruptedException, ExecutionException {
            if (!onceWork.isFinish()) {
                onceWork.awaitFinish();
            }
            return onceWork.getWrappers();
        }

        /**
         * 避免线程爆炸，该方法不予单独开线程，而是单线程{@link Thread#sleep(long)}每睡一段时间检查一次。
         */
        @Override
        public Map<String, WorkerWrapper<?, ?>> get(long timeout,
                                                    TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            final long millis   = Objects.requireNonNull(unit).toMillis(timeout);
            final long interval = Math.max(1, Math.min(millis, sleepCheckInterval.apply(millis)));
            for (int i = 0; interval * i < millis; i++) {
                if (onceWork.isFinish()) {
                    return onceWork.getWrappers();
                }
                Thread.sleep(interval);
            }
            throw new TimeoutException(
                    "onceWork.asFuture.get(long,TimeUnit) out of time limit(" +
                            timeout + "," + unit + ") , this is " + this);
        }

        @Override
        public String toString() {
            return "(asFuture from " + onceWork + ")@" + Integer.toHexString(this.hashCode());
        }
    }

    abstract class AbstractOnceWork implements OnceWork {
        protected final String workId;

        public AbstractOnceWork(String workId) {
            this.workId = workId;
        }

        @Override
        public String workId() {
            return workId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof OnceWork)) {
                return false;
            }
            OnceWork _o = (OnceWork) o;
            return Objects.equals(_o.workId(), this.workId());
        }

        @Override
        public int hashCode() {
            return workId().hashCode();
        }

        @Override
        public String toString() {
            final boolean finish;
            final StringBuilder sb = new StringBuilder(48)
                    .append(this.getClass().getSimpleName())
                    .append("{isFinish=").append(finish = isFinish())
                    .append(", hasTimeout=").append(hasTimeout())
                    .append(", allSuccess=").append(allSuccess())
                    .append(", getStartTime=").append(getStartTime())
                    .append(", isCancelled=").append(isCancelled())
                    .append(", isWaitingCancel=").append(isWaitingCancel());
            if (finish) {
                sb.append(", getFinishTime=").append(getFinishTime());
            }
            return sb
                    .append(", wrappers::getId=").append(getWrappers().keySet())
                    .append('}').toString();
        }
    }

    class Impl extends AbstractOnceWork {
        protected final WorkerWrapperGroup group;

        public Impl(WorkerWrapperGroup group, String workId) {
            super(workId);
            this.group = group;
        }

        @Override
        public boolean isFinish() {
            return group.getEndCDL().getCount() > 0;
        }

        @Override
        public void awaitFinish() throws InterruptedException {
            group.getEndCDL().await();
        }

        @Override
        public boolean hasTimeout() {
            return group.getAnyTimeout().get();
        }

        @Override
        public Map<String, WorkerWrapper<?, ?>> getWrappers() {
            return group.getForParamUseWrappers();
        }

        @Override
        public long getStartTime() {
            return group.getGroupStartTime();
        }

        @Override
        public long getFinishTime() {
            if (isFinish()) {
                throw new IllegalStateException("work not finish.");
            }
            return group.getFinishTime();
        }

        @Override
        public boolean isCancelled() {
            return group.isCancelled();
        }

        @Override
        public boolean isWaitingCancel() {
            return group.isWaitingCancel();
        }

        @Override
        public void pleaseCancel() {
            group.pleaseCancel();
        }
    }

    class EmptyWork extends AbstractOnceWork {
        private final long initTime = SystemClock.now();

        public EmptyWork(String workId) {
            super(workId);
        }

        @Override
        public boolean isFinish() {
            return true;
        }

        @Override
        public void awaitFinish() {
            // do nothing
        }

        @Override
        public boolean hasTimeout() {
            return false;
        }

        @Override
        public Map<String, WorkerWrapper<?, ?>> getWrappers() {
            return Collections.emptyMap();
        }

        @Override
        public long getStartTime() {
            return initTime;
        }

        @Override
        public long getFinishTime() {
            return initTime;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isWaitingCancel() {
            return false;
        }

        @Override
        public void pleaseCancel() {
        }

        @Override
        public String toString() {
            return "(it's empty work)";
        }
    }
}
