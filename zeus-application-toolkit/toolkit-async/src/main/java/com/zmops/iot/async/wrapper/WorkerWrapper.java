package com.zmops.iot.async.wrapper;

import com.zmops.iot.async.callback.DefaultCallback;
import com.zmops.iot.async.callback.ICallback;
import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.exception.CancelException;
import com.zmops.iot.async.exception.EndsNormallyException;
import com.zmops.iot.async.exception.SkippedException;
import com.zmops.iot.async.executor.PollingCenter;
import com.zmops.iot.async.executor.timer.SystemClock;
import com.zmops.iot.async.worker.ResultState;
import com.zmops.iot.async.worker.WorkResult;
import com.zmops.iot.async.wrapper.strategy.WrapperStrategy;
import com.zmops.iot.async.wrapper.strategy.depend.DependMustStrategyMapper;
import com.zmops.iot.async.wrapper.strategy.depend.DependOnUpWrapperStrategyMapper;
import com.zmops.iot.async.wrapper.strategy.depend.DependenceAction;
import com.zmops.iot.async.wrapper.strategy.depend.DependenceStrategy;
import com.zmops.iot.async.wrapper.strategy.skip.SkipStrategy;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.zmops.iot.async.wrapper.WorkerWrapper.State.*;

/**
 * 对每个worker及callback进行包装，一对一
 * <p/>
 * v1.5时将其抽取为抽象类，以解耦并提高扩展性。
 *
 * @author wuweifeng wrote on 2019-11-19.
 */
@SuppressWarnings("AlibabaAbstractClassShouldStartWithAbstractNaming")
public abstract class WorkerWrapper<T, V> {
    // ========== 固定属性 ==========

    /**
     * 该wrapper的唯一标识
     */
    protected final String          id;
    protected final IWorker<T, V>   worker;
    protected final ICallback<T, V> callback;
    /**
     * 各种策略的封装类。
     */
    private final   WrapperStrategy wrapperStrategy;
    /**
     * 是否允许被打断
     */
    protected final boolean         allowInterrupt;
    /**
     * 是否启动超时检查
     */
    final           boolean         enableTimeout;
    /**
     * 超时时间长度
     */
    final           long            timeoutLength;
    /**
     * 超时时间单位
     */
    final           TimeUnit        timeoutUnit;

    // ========== 临时属性 ==========

    /**
     * worker将来要处理的param
     */
    protected volatile T                              param;
    /**
     * 原子设置wrapper的状态
     * <p>
     * {@link State}此枚举类枚举了state值所代表的状态枚举。
     */
    protected final    AtomicInteger                  state           = new AtomicInteger(State.BUILDING.id);
    /**
     * 该值将在{@link IWorker#action(Object, Map)}进行时设为当前线程，在任务开始前或结束后都为null。
     */
    protected final    AtomicReference<Thread>        doWorkingThread = new AtomicReference<>();
    /**
     * 也是个钩子变量，用来存临时的结果
     */
    protected final    AtomicReference<WorkResult<V>> workResult      = new AtomicReference<>(null);

    WorkerWrapper(String id,
                  IWorker<T, V> worker,
                  ICallback<T, V> callback,
                  boolean allowInterrupt,
                  boolean enableTimeout,
                  long timeoutLength,
                  TimeUnit timeoutUnit,
                  WrapperStrategy wrapperStrategy
    ) {
        if (worker == null) {
            throw new NullPointerException("async.worker is null");
        }
        this.worker = worker;
        this.id = id;
        //允许不设置回调
        if (callback == null) {
            //noinspection unchecked
            callback = (ICallback<T, V>) DefaultCallback.getInstance();
        }
        this.callback = callback;
        this.allowInterrupt = allowInterrupt;
        this.enableTimeout = enableTimeout;
        this.timeoutLength = timeoutLength;
        this.timeoutUnit = timeoutUnit;
        this.wrapperStrategy = wrapperStrategy;
    }

    WorkerWrapper(String id,
                  IWorker<T, V> worker,
                  ICallback<T, V> callback,
                  boolean allowInterrupt,
                  boolean enableTimeout,
                  long timeoutLength,
                  TimeUnit timeoutUnit) {
        this(id, worker, callback, allowInterrupt, enableTimeout, timeoutLength, timeoutUnit, new StableWrapperStrategy());
    }

    // ========== public ==========

    /**
     * 外部调用本线程运行此wrapper的入口方法。
     * 该方法将会确定这组wrapper所属的group。
     *
     * @param executorService 该ExecutorService将成功运行后，在nextWrapper有多个时被使用于多线程调用。
     * @param remainTime      剩下的时间
     * @param group           wrapper组
     * @throws IllegalStateException 当wrapper正在building状态时被启动，则会抛出该异常。
     */
    public void work(ExecutorService executorService,
                     long remainTime,
                     WorkerWrapperGroup group) {
        work(executorService, null, remainTime, group);
    }

    public String getId() {
        return id;
    }

    /**
     * 返回{@link #workResult}的值。
     * 若调用此方法时workResult还未设置，将会返回{@link WorkResult#defaultResult()}。
     */
    public WorkResult<V> getWorkResult() {
        WorkResult<V> res = workResult.get();
        return res == null ? WorkResult.defaultResult() : res;
    }

    public void setParam(T param) {
        this.param = param;
    }

    public State getState() {
        return of(state.get());
    }

    /**
     * 获取下游Wrapper
     */
    public abstract Set<WorkerWrapper<?, ?>> getNextWrappers();

    /**
     * 获取上游wrapper
     */
    public abstract Set<WorkerWrapper<?, ?>> getDependWrappers();

    /**
     * 获取本wrapper的超时情况。如有必要还会修改wrapper状态。
     *
     * @param withEndIt       如果为true，在检查出已经超时的时候，会将其快速结束。
     * @param startTime       起始时间
     * @param totalTimeLength 总任务时长
     * @return 超时返回-1L，结束但未超时返回0L，尚未结束且未超时返回与deadline的差值
     * <p>
     * 当没有超时，若该wrapper已经结束但没有超时，返回 0L 。
     * <p>
     * 如果该wrapper单独了设置超时策略并正在运行，返回距离超时策略限时相差的毫秒值。
     * 例如设置10ms超时，此时已经开始3ms，则返回 7L。
     * 如果此差值<1，则返回 1L。
     * <p>
     * 如果已经超时，则返回 -1L。
     * </p>
     */
    public long checkTimeout(boolean withEndIt, long startTime, long totalTimeLength) {
        do {
            WorkResult<V> _workResult = workResult.get();
            // 如果已经有结果了，就根据结果值判断
            if (_workResult != null) {
                return _workResult.getResultState() == ResultState.TIMEOUT ? -1L : 0L;
            }
            // 如果还没有出结果
            // 判断是否超时
            long now = SystemClock.now();
            if (totalTimeLength < now - startTime
                    || enableTimeout && timeoutUnit.toMillis(timeoutLength) < now - startTime) {
                // 如果需要处理该wrapper的状态
                if (withEndIt) {
                    // CAS一个超时的结果
                    if (!workResult.compareAndSet(
                            null,
                            new WorkResult<>(null, ResultState.TIMEOUT, null))
                    ) {
                        // 就在想CAS的时候，出结果了，就采用新的结果重新判断一次
                        continue;
                    }
                    fastFail(true, null, false);
                }
                return -1L;
            }
            // 正在运行，尚未超时
            else {
                return Math.max(1, startTime + totalTimeLength - now);
            }
        } while (true);
    }

    /**
     * 直接取消wrapper运行。
     * 如果状态在 {@link State#states_of_beforeWorkingEnd}中，则调用 {@link #fastFail(boolean, Exception, boolean)}。
     */
    public void cancel() {
        if (State.setState(state, State.states_of_beforeWorkingEnd, State.SKIP, null)) {
            fastFail(false, new CancelException(), true);
        }
    }

    public WrapperStrategy getWrapperStrategy() {
        return wrapperStrategy;
    }

    // ========== protected ==========

    /**
     * 工作的核心方法。
     *
     * @param fromWrapper 代表这次work是由哪个上游wrapper发起的。如果是首个Wrapper则为null。
     * @param remainTime  剩余时间。
     * @throws IllegalStateException 当wrapper正在building状态时被启动，则会抛出该异常。
     */
    protected void work(ExecutorService executorService,
                        WorkerWrapper<?, ?> fromWrapper,
                        long remainTime,
                        WorkerWrapperGroup group
    ) {
        long now = SystemClock.now();
        // ================================================
        // 以下是一些lambda。
        // 因为抽取成方法反而不好传参、污染类方法，所以就这么干了
        final Consumer<Boolean> __function__callbackResult =
                success -> {
                    WorkResult<V> _workResult = getWorkResult();
                    try {
                        callback.result(success, param, _workResult);
                    } catch (Exception e) {
                        if (setState(state, State.states_of_skipOrAfterWork, State.ERROR, null)) {
                            fastFail(false, e, _workResult.getEx() instanceof EndsNormallyException);
                        }
                    }
                };
        final Runnable __function__callbackResultOfFalse_beginNext =
                () -> {
                    __function__callbackResult.accept(false);
                    beginNext(executorService, now, remainTime, group);
                };
        final BiConsumer<Boolean, Exception> __function__fastFail_callbackResult$false_beginNext =
                (fastFail_isTimeout, fastFail_exception) -> {
                    boolean isEndsNormally = fastFail_exception instanceof EndsNormallyException;
                    fastFail(fastFail_isTimeout && !isEndsNormally, fastFail_exception, isEndsNormally);
                    __function__callbackResultOfFalse_beginNext.run();
                };
        final Runnable __function__doWork =
                () -> {
                    if (setState(state, State.STARTED, State.WORKING)) {
                        try {
                            fire(group);
                        } catch (Exception e) {
                            if (setState(state, State.WORKING, State.ERROR)) {
                                __function__fastFail_callbackResult$false_beginNext.accept(false, e);
                            }
                            return;
                        }
                    }
                    if (setState(state, State.WORKING, State.AFTER_WORK)) {
                        __function__callbackResult.accept(true);
                        beginNext(executorService, now, remainTime, group);
                    }
                };
        // ================================================
        // 开始执行
        try {
            if (isState(state, BUILDING)) {
                throw new IllegalStateException("wrapper can't work because state is BUILDING ! wrapper is " + this);
            }
            // 判断是否整组取消
            if (group.isWaitingCancel() || group.isCancelled()) {
                cancel();
                return;
            }
            // 总的已经超时了，就快速失败，进行下一个
            if (remainTime <= 0) {
                if (setState(state, states_of_beforeWorkingEnd, ERROR, null)) {
                    __function__fastFail_callbackResult$false_beginNext.accept(true, null);
                }
                return;
            }
            // 如果自己已经执行过了。
            // 可能有多个依赖，其中的一个依赖已经执行完了，并且自己也已开始执行或执行完毕。当另一个依赖执行完毕，又进来该方法时，就不重复处理了
            final AtomicReference<State> oldStateRef = new AtomicReference<>(null);
            if (!setState(state, states_of_notWorked, STARTED, oldStateRef::set)) {
                return;
            }
            // 如果wrapper是第一次，要调用callback.begin
            if (oldStateRef.get() == INIT) {
                try {
                    callback.begin();
                } catch (Exception e) {
                    // callback.begin 发生异常
                    if (setState(state, states_of_beforeWorkingEnd, ERROR, null)) {
                        __function__fastFail_callbackResult$false_beginNext.accept(false, e);
                    }
                    return;
                }
            }

            //如果fromWrapper为null，说明自己就是第一批要执行的
            if (fromWrapper == null) {
                // 首当其冲，开始工作
                __function__doWork.run();
                return;
            }

            // 每个线程都需要判断是否要跳过自己，该方法可能会跳过正在工作的自己。
            final WrapperStrategy wrapperStrategy = getWrapperStrategy();
            if (wrapperStrategy.shouldSkip(getNextWrappers(), this, fromWrapper)) {
                if (setState(state, STARTED, SKIP)) {
                    __function__fastFail_callbackResult$false_beginNext.accept(false, new SkippedException());
                }
                return;
            }

            // 如果是由其他wrapper调用而运行至此，则使用策略器决定自己的行为
            DependenceAction.WithProperty judge =
                    wrapperStrategy.judgeAction(getDependWrappers(), this, fromWrapper);
            switch (judge.getDependenceAction()) {
                case TAKE_REST:
                    return;
                case FAST_FAIL:
                    if (setState(state, STARTED, ERROR)) {
                        //  根据FAST_FAIL.fastFailException()设置的属性值来设置fastFail方法的参数
                        ResultState resultState = judge.getResultState();
                        __function__fastFail_callbackResult$false_beginNext.accept(
                                resultState == ResultState.TIMEOUT,
                                judge.getFastFailException()
                        );
                    }
                    return;
                case START_WORK:
                    __function__doWork.run();
                    return;
                case JUDGE_BY_AFTER:
                default:
                    throw new IllegalStateException(
                            "策略配置错误，不应当在WorkerWrapper中返回JUDGE_BY_AFTER或其他无效值 : this=" + this +
                                    ",fromWrapper=" + fromWrapper);
            }
        } catch (Exception e) {
            // wrapper本身抛出了不该有的异常
            setState(state, states_all, ERROR, null);
            NotExpectedException ex = new NotExpectedException(e, this);
            workResult.set(new WorkResult<>(null, ResultState.EXCEPTION, ex));
            __function__fastFail_callbackResult$false_beginNext.accept(false, ex);
        }
    }


    /**
     * 本工作线程执行自己的job.
     * <p/>
     * 本方法不负责校验状态。请在调用前自行检验
     */
    protected void fire(WorkerWrapperGroup group) {
        try {
            doWorkingThread.set(Thread.currentThread());
            //执行耗时操作
            V result = worker.action(param, group.getForParamUseWrappers());
            workResult.compareAndSet(
                    null,
                    new WorkResult<>(result, ResultState.SUCCESS)
            );
        } finally {
            doWorkingThread.set(null);
        }
    }

    /**
     * 快速失败。
     * 该方法不负责检查状态，请自行控制。
     *
     * @param isTimeout      是否是因为超时而快速失败
     * @param e              设置异常信息到{@link WorkResult#getEx()}
     * @param isEndsNormally 是否是因正常情况正常而结束，例如跳过{@link SkippedException}、取消{@link CancelException}。
     */
    protected void fastFail(boolean isTimeout, Exception e, boolean isEndsNormally) {
        // 试图打断正在执行{@link IWorker#action(Object, Map)}的线程
        Thread _doWorkingThread;
        if ((_doWorkingThread = this.doWorkingThread.get()) != null
                // 不会打断自己
                && !Objects.equals(Thread.currentThread(), _doWorkingThread)) {
            _doWorkingThread.interrupt();
        }
        // 尚未处理过结果则设置
        workResult.compareAndSet(null, new WorkResult<>(
                worker.defaultValue(),
                isTimeout ? ResultState.TIMEOUT : (isEndsNormally ? ResultState.DEFAULT : ResultState.EXCEPTION),
                e
        ));
    }

    /**
     * 进行下一个任务
     * <p/>
     * 本方法不负责校验状态。请在调用前自行检验
     */
    protected void beginNext(ExecutorService executorService, long now, long remainTime, WorkerWrapperGroup group) {
        //花费的时间
        final long               costTime       = SystemClock.now() - now;
        final long               nextRemainTIme = remainTime - costTime;
        Set<WorkerWrapper<?, ?>> nextWrappers   = getNextWrappers();
        if (nextWrappers == null) {
            PollingCenter.getInstance().checkGroup(group.new CheckFinishTask());
            return;
        }
        // nextWrappers只有一个，就用本线程继续跑。
        if (nextWrappers.size() == 1) {
            WorkerWrapper<?, ?> next = null;
            try {
                next = nextWrappers.stream().findFirst().get();
                group.addWrapper(next);
                setState(state, AFTER_WORK, SUCCESS);
            } finally {
                PollingCenter.getInstance().checkGroup(group.new CheckFinishTask());
                if (next != null) {
                    next.work(executorService, this, nextRemainTIme, group);
                }
            }
        }
        // nextWrappers有多个
        else {
            try {
                group.addWrapper(nextWrappers);
                nextWrappers.forEach(next -> executorService.submit(() ->
                        next.work(executorService, this, nextRemainTIme, group))
                );
                setState(state, AFTER_WORK, SUCCESS);
            } finally {
                PollingCenter.getInstance().checkGroup(group.new CheckFinishTask());
            }
        }

    }

    // ========== hashcode and equals ==========

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    /**
     * {@code return id.hashCode();}返回id值的hashcode
     */
    @Override
    public int hashCode() {
        // final String id can use to .hashcode() .
        return id.hashCode();
    }

    // ========== builder ==========

    public static <T, V> WorkerWrapperBuilder<T, V> builder() {
        return new Builder<>();
    }

    /**
     * 自v1.5，该类被抽取到{@link StableWorkerWrapperBuilder}抽象类，兼容之前的版本。
     */
    public static class Builder<W, C> extends StableWorkerWrapperBuilder<W, C, Builder<W, C>> {
        /**
         * @deprecated 建议使用 {@link #builder()}返回{@link WorkerWrapperBuilder}接口，以调用v1.5之后的规范api
         */
        @SuppressWarnings("DeprecatedIsStillUsed")
        @Deprecated
        public Builder() {
        }
    }

    // ========== package access methods ==========

    abstract void setNextWrappers(Set<WorkerWrapper<?, ?>> nextWrappers);

    abstract void setDependWrappers(Set<WorkerWrapper<?, ?>> dependWrappers);

    // ========== toString ==========

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(256)
                .append(this.getClass().getSimpleName())
                .append("{id=").append(id)
                .append(", state=").append(of(state.get()))
                .append(", param=").append(param)
                .append(", workResult=").append(workResult)
                .append(", allowInterrupt=").append(allowInterrupt)
                .append(", enableTimeout=").append(enableTimeout)
                .append(", timeoutLength=").append(timeoutLength)
                .append(", timeoutUnit=").append(timeoutUnit)
                // 防止循环引用，这里只输出相关Wrapper的id
                .append(", dependWrappers::getId=[");
        final Set<WorkerWrapper<?, ?>> dependWrappers = getDependWrappers();
        dependWrappers.stream().map(WorkerWrapper::getId).forEach(wrapperId -> sb.append(wrapperId).append(", "));
        if (dependWrappers.size() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb
                .append("], nextWrappers::getId=[");
        final Set<WorkerWrapper<?, ?>> nextWrappers = getNextWrappers();
        nextWrappers.stream().map(WorkerWrapper::getId).forEach(wrapperId -> sb.append(wrapperId).append(", "));
        if (nextWrappers.size() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb
                .append("], doWorkingThread=").append(doWorkingThread.get())
                .append(", worker=").append(worker)
                .append(", callback=").append(callback)
                .append(", wrapperStrategy=").append(wrapperStrategy)
                .append('}');
        return sb.toString();
    }

    /**
     * 一个通用的策略器实现类，提供了修改的功能。并兼容之前的代码。
     */
    public static class StableWrapperStrategy extends WrapperStrategy.AbstractWrapperStrategy {
        private DependOnUpWrapperStrategyMapper dependOnUpWrapperStrategyMapper;
        private DependMustStrategyMapper        dependMustStrategyMapper;
        private DependenceStrategy              dependenceStrategy;
        private SkipStrategy                    skipStrategy;

        @Override
        public DependOnUpWrapperStrategyMapper getDependWrapperStrategyMapper() {
            return dependOnUpWrapperStrategyMapper;
        }

        @Override
        public void setDependWrapperStrategyMapper(DependOnUpWrapperStrategyMapper dependOnUpWrapperStrategyMapper) {
            this.dependOnUpWrapperStrategyMapper = dependOnUpWrapperStrategyMapper;
        }

        @SuppressWarnings("deprecation")
        @Override
        public DependMustStrategyMapper getDependMustStrategyMapper() {
            return dependMustStrategyMapper;
        }

        @Override
        public void setDependMustStrategyMapper(DependMustStrategyMapper dependMustStrategyMapper) {
            this.dependMustStrategyMapper = dependMustStrategyMapper;
        }

        @Override
        public DependenceStrategy getDependenceStrategy() {
            return dependenceStrategy;
        }

        @Override
        public void setDependenceStrategy(DependenceStrategy dependenceStrategy) {
            this.dependenceStrategy = dependenceStrategy;
        }

        @Override
        public SkipStrategy getSkipStrategy() {
            return skipStrategy;
        }

        @Override
        public void setSkipStrategy(SkipStrategy skipStrategy) {
            this.skipStrategy = skipStrategy;
        }
    }

    /**
     * state状态枚举工具类
     */
    public enum State {
        /**
         * 初始化中，builder正在设置其数值
         */
        BUILDING(-1),
        /**
         * 初始化完成，但是还未执行过。
         */
        INIT(0),
        /**
         * 执行过。
         * 即至少进行了一次各种判定，例如判断 是否跳过/是否启动工作
         */
        STARTED(1),
        /**
         * 工作状态
         */
        WORKING(2),
        /**
         * 工作完成后的收尾工作，例如调用下游wrapper
         */
        AFTER_WORK(3),
        /**
         * wrapper成功执行结束
         */
        SUCCESS(4),
        /**
         * wrapper失败了
         */
        ERROR(5),
        /**
         * wrapper被跳过
         */
        SKIP(6);

        // public

        public boolean finished() {
            return this == SUCCESS || this == ERROR || this == SKIP;
        }

        // package

        State(int id) {
            this.id = id;
        }

        final int id;

        // package-static

        static final State[] states_of_notWorked = new State[]{INIT, STARTED};

        static final State[] states_of_skipOrAfterWork = new State[]{SKIP, AFTER_WORK};

        static final State[] states_of_beforeWorkingEnd = new State[]{INIT, STARTED, WORKING};

        static final State[] states_all = new State[]{BUILDING, INIT, STARTED, WORKING, AFTER_WORK, SUCCESS, ERROR, SKIP};

        /**
         * 自旋+CAS的设置状态，如果状态不在exceptValues返回内 或 没有设置成功，则返回false。
         *
         * @param state        {@link WorkerWrapper#state} 要被修改的AtomicInteger引用
         * @param exceptValues 期望的值数组，任何满足该值的state都会被修改
         * @param newValue     新值
         * @param withOperate  如果该参数不为null并且成功设置，该函数将会被执行，其参数为wrapper原子设置之前的旧状态。
         *                     之所以需要这个参数，是因为当except值有多个时，无法确定是哪个值被原子修改了。
         * @return 返回是否成功设置。
         */
        static boolean setState(AtomicInteger state,
                                State[] exceptValues,
                                State newValue,
                                Consumer<State> withOperate) {
            int     current;
            boolean inExcepts;
            while (true) {
                // 判断当前值是否在exceptValues范围内
                current = state.get();
                inExcepts = false;
                for (State exceptValue : exceptValues) {
                    if (inExcepts = current == exceptValue.id) {
                        break;
                    }
                }
                // 如果不在 exceptValues 范围内，直接返回false。
                if (!inExcepts) {
                    return false;
                }
                // 如果在 exceptValues 范围，cas成功返回true，失败（即当前值被修改）则自旋。
                if (state.compareAndSet(current, newValue.id)) {
                    if (withOperate != null) {
                        withOperate.accept(of(current));
                    }
                    return true;
                }
            }
        }

        /**
         * 自旋+CAS的设置状态，如果状态不在exceptValues返回内 或 没有设置成功自旋后不在范围内，则返回false。
         *
         * @param state       {@link WorkerWrapper#state} 要被修改的AtomicInteger引用
         * @param exceptValue 期望的值
         * @param newValue    新值
         * @return 返回是否成功设置。
         */
        static boolean setState(AtomicInteger state,
                                State exceptValue,
                                State newValue) {
            int current;
            // 如果当前值与期望值相同
            while ((current = state.get()) == exceptValue.id) {
                // 则尝试CAS设置新值
                if (state.compareAndSet(current, newValue.id)) {
                    return true;
                }
                // 如果当前值被改变，则尝试自旋
            }
            // 如果当前值与期望值不相同了，就直接返回false
            return false;
        }

        /**
         * 自旋+CAS的判断是否在这些excepts范围内
         *
         * @param excepts 范围。
         */
        @SuppressWarnings("unused")
        static boolean inStates(AtomicInteger state, State... excepts) {
            int     current;
            boolean inExcepts;
            while (true) {
                current = state.get();
                inExcepts = false;
                for (State except : excepts) {
                    if (current == except.id) {
                        inExcepts = true;
                        break;
                    }
                }
                if (state.get() == current) {
                    return inExcepts;
                }
            }
        }

        /**
         * CAS的判断是否是某个状态
         */
        static boolean isState(AtomicInteger state, @SuppressWarnings("SameParameterValue") State except) {
            return state.compareAndSet(except.id, except.id);
        }

        static State of(int id) {
            return id2state.get(id);
        }

        static final Map<Integer, State> id2state;

        static {
            HashMap<Integer, State> map = new HashMap<>();
            for (State s : State.values()) {
                map.put(s.id, s);
            }
            id2state = Collections.unmodifiableMap(map);
        }


    }

    /**
     * 这是因未知错误而引发的异常
     */
    public static class NotExpectedException extends Exception {
        public NotExpectedException(Throwable cause, WorkerWrapper<?, ?> wrapper) {
            super("It's should not happened Exception . wrapper is " + wrapper, cause);
        }
    }
}
