package com.zmops.iot.async.executor;


import com.zmops.iot.async.callback.DefaultGroupCallback;
import com.zmops.iot.async.callback.IGroupCallback;
import com.zmops.iot.async.executor.timer.SystemClock;
import com.zmops.iot.async.worker.OnceWork;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.async.wrapper.WorkerWrapperGroup;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 核心工具类。
 *
 * @author wuweifeng wrote on 2019-12-18
 * @version 1.0
 */
public class Async {

    // ========================= 任务执行核心代码 =========================

    /**
     * {@link #work(long, ExecutorService, Collection, String)}方法的简易封装。
     * 使用uuid作为工作id。使用{@link #getCommonPool()}作为线程池。
     */
    public static OnceWork work(long timeout,
                                Collection<? extends WorkerWrapper<?, ?>> workerWrappers) {
        return work(timeout, getCommonPool(), workerWrappers);
    }

    /**
     * {@link #work(long, ExecutorService, Collection, String)}方法的简易封装。
     * 可变参式传入。使用uuid作为工作id。使用{@link #getCommonPool()}作为线程池。
     */
    public static OnceWork work(long timeout,
                                WorkerWrapper<?, ?>... workerWrappers) {
        return work(timeout, getCommonPool(), workerWrappers);
    }

    /**
     * {@link #work(long, ExecutorService, Collection, String)}方法的简易封装。
     * 可变参式传入。使用uuid作为工作id。
     */
    public static OnceWork work(long timeout,
                                ExecutorService executorService,
                                WorkerWrapper<?, ?>... workerWrappers) {
        return work(timeout, executorService, Arrays.asList(
                Objects.requireNonNull(workerWrappers, "workerWrappers array is null")));
    }

    /**
     * {@link #work(long, ExecutorService, Collection, String)}方法的简易封装。
     * 省略工作id，使用uuid。
     */
    public static OnceWork work(long timeout,
                                ExecutorService executorService,
                                Collection<? extends WorkerWrapper<?, ?>> workerWrappers) {
        return work(timeout, executorService, workerWrappers, UUID.randomUUID().toString());
    }

    /**
     * <b>核心方法。该方法不是同步阻塞执行的。</b>如果想要同步阻塞执行，则调用返回值的{@link OnceWork#awaitFinish()}即可。
     *
     * @param timeout         全组超时时间
     * @param executorService 执行线程池
     * @param workerWrappers  任务容器集合
     * @param workId          本次工作id
     * @return 返回 {@link OnceWork}任务句柄对象。
     */
    public static OnceWork work(long timeout,
                                ExecutorService executorService,
                                Collection<? extends WorkerWrapper<?, ?>> workerWrappers,
                                String workId) {
        if (workerWrappers == null || workerWrappers.isEmpty()) {
            return OnceWork.emptyWork(workId);
        }
        //保存上次执行的线程池变量（为了兼容以前的旧功能）
        Async.lastExecutorService.set(Objects.requireNonNull(executorService, "ExecutorService is null ! "));
        final WorkerWrapperGroup group    = new WorkerWrapperGroup(SystemClock.now(), timeout);
        final OnceWork.Impl      onceWork = new OnceWork.Impl(group, workId);
        group.addWrapper(workerWrappers);
        workerWrappers.forEach(wrapper -> {
            if (wrapper == null) {
                return;
            }
            executorService.submit(() -> wrapper.work(executorService, timeout, group));
        });
        return onceWork;
    }

    // ========================= 设置/属性选项 =========================

    /**
     * 默认线程池。
     * <p>
     * 在v1.4及之前，该COMMON_POLL是被写死的。
     * <p>
     * 自v1.5后：
     * 该线程池将会在第一次调用{@link #getCommonPool()}时懒加载。
     * tip:
     * 要注意，{@link #work(long, WorkerWrapper[])}、{@link #work(long, Collection)}这些方法，
     * 不传入线程池就会默认调用{@link #getCommonPool()}，就会初始化线程池。
     * <p>
     * 该线程池将会给线程取名为asyncTool-commonPool-thread-0（数字不重复）。
     * </p>
     */
    private static volatile ThreadPoolExecutor COMMON_POOL;

    /**
     * 在以前（及现在）的版本中：
     * 当执行{@link #beginWork(long, ExecutorService, Collection)}方法时，ExecutorService将会被记录下来。
     * <p/>
     * 注意，这里是个static，也就是只能有一个线程池。用户自定义线程池时，也只能定义一个
     *
     * @deprecated 不明意义、毫无用处的字段。记录之前使用的线程池没啥意义。
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    private static final AtomicReference<ExecutorService> lastExecutorService = new AtomicReference<>(null);

    /**
     * 该方法将会返回{@link #COMMON_POOL}，如果还未初始化则会懒加载初始化后再返回。
     * 详情参考{@link #COMMON_POOL}上的注解
     */
    public static ThreadPoolExecutor getCommonPool() {
        if (COMMON_POOL == null) {
            synchronized (Async.class) {
                if (COMMON_POOL == null) {
                    COMMON_POOL = new ThreadPoolExecutor(
                            Runtime.getRuntime().availableProcessors() * 2,
                            1024,
                            15L,
                            TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(),
                            new ThreadFactory() {
                                private final AtomicLong threadCount = new AtomicLong(0);

                                @Override
                                public Thread newThread(Runnable r) {
                                    Thread t = new Thread(r,
                                            "asyncTool-commonPool-thread-" + threadCount.getAndIncrement());
                                    t.setDaemon(true);
                                    return t;
                                }

                                @Override
                                public String toString() {
                                    return "asyncTool-commonPool-threadFactory";
                                }
                            }
                    ) {
                        @Override
                        public String toString() {
                            return "asyncTool-commonPool";
                        }
                    };
                }
            }
        }
        return COMMON_POOL;
    }

    /**
     * @deprecated 不明意义的输出信息的方法
     */
    @Deprecated
    public static String getThreadCount() {
        return "activeCount=" + COMMON_POOL.getActiveCount() +
                ",completedCount=" + COMMON_POOL.getCompletedTaskCount() +
                ",largestCount=" + COMMON_POOL.getLargestPoolSize();
    }

    /**
     * @param now 是否立即关闭
     * @return 如果尚未调用过{@link #getCommonPool()}，即没有初始化默认线程池，返回false。否则返回true。
     */
    @SuppressWarnings("unused")
    public static synchronized boolean shutDownCommonPool(boolean now) {
        if (COMMON_POOL == null) {
            return false;
        }
        if (!COMMON_POOL.isShutdown()) {
            if (now) {
                COMMON_POOL.shutdownNow();
            } else {
                COMMON_POOL.shutdown();
            }
        }
        return true;
    }

    // ========================= deprecated =========================

    /**
     * 同步执行一次任务。
     *
     * @return 只要执行未超时，就返回true。
     * @deprecated 已经被 {@link #work(long, ExecutorService, Collection, String)}方法取代。
     */
    @Deprecated
    public static boolean beginWork(long timeout,
                                    ExecutorService executorService,
                                    Collection<? extends WorkerWrapper<?, ?>> workerWrappers)
            throws InterruptedException {
        final OnceWork work = work(timeout, executorService, workerWrappers);
        work.awaitFinish();
        return work.hasTimeout();
    }

    /**
     * 同步执行一次任务。
     * 如果想自定义线程池，请传pool。不自定义的话，就走默认的COMMON_POOL
     *
     * @deprecated 已经被 {@link #work(long, ExecutorService, Collection, String)}方法取代。
     */
    @Deprecated
    public static boolean beginWork(long timeout, ExecutorService executorService, WorkerWrapper... workerWrapper)
            throws ExecutionException, InterruptedException {
        if (workerWrapper == null || workerWrapper.length == 0) {
            return false;
        }
        Set workerWrappers = Arrays.stream(workerWrapper).collect(Collectors.toSet());
        //noinspection unchecked
        return beginWork(timeout, executorService, workerWrappers);
    }

    /**
     * 同步阻塞,直到所有都完成,或失败
     *
     * @deprecated 已经被 {@link #work(long, ExecutorService, Collection, String)}方法取代。
     */
    @Deprecated
    public static boolean beginWork(long timeout, WorkerWrapper... workerWrapper) throws ExecutionException, InterruptedException {
        return beginWork(timeout, getCommonPool(), workerWrapper);
    }

    /**
     * @deprecated 已经被 {@link #work(long, ExecutorService, Collection, String)}方法取代。
     */
    @Deprecated
    public static void beginWorkAsync(long timeout, IGroupCallback groupCallback, WorkerWrapper... workerWrapper) {
        beginWorkAsync(timeout, getCommonPool(), groupCallback, workerWrapper);
    }

    /**
     * 异步执行,直到所有都完成,或失败后，发起回调
     *
     * @deprecated 已经被 {@link #work(long, ExecutorService, Collection, String)}方法取代。
     */
    @Deprecated
    public static void beginWorkAsync(long timeout, ExecutorService executorService, IGroupCallback groupCallback, WorkerWrapper... workerWrapper) {
        if (groupCallback == null) {
            groupCallback = new DefaultGroupCallback();
        }
        IGroupCallback finalGroupCallback = groupCallback;
        if (executorService != null) {
            executorService.submit(() -> {
                try {
                    boolean success = beginWork(timeout, executorService, workerWrapper);
                    if (success) {
                        finalGroupCallback.success(Arrays.asList(workerWrapper));
                    } else {
                        finalGroupCallback.failure(Arrays.asList(workerWrapper), new TimeoutException());
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    finalGroupCallback.failure(Arrays.asList(workerWrapper), e);
                }
            });
        } else {
            final ExecutorService commonPool = getCommonPool();
            commonPool.submit(() -> {
                try {
                    boolean success = beginWork(timeout, commonPool, workerWrapper);
                    if (success) {
                        finalGroupCallback.success(Arrays.asList(workerWrapper));
                    } else {
                        finalGroupCallback.failure(Arrays.asList(workerWrapper), new TimeoutException());
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                    finalGroupCallback.failure(Arrays.asList(workerWrapper), e);
                }
            });
        }

    }

    /**
     * 关闭上次使用的线程池
     *
     * @deprecated 因此在v1.5时加上了废弃注解。
     * <p>
     * 这是一个很迷的方法，多线程时调用该方法的{@link #lastExecutorService}可能会被别的线程修改而引发不必要、不可控的错误。仅建议用来测试。
     * 另外，该方法现在不会关闭默认线程池。
     * </p>
     */
    @Deprecated
    public static void shutDown() {
        final ExecutorService last = lastExecutorService.get();
        if (last != COMMON_POOL) {
            shutDown(last);
        }
    }

    /**
     * 关闭指定的线程池
     *
     * @param executorService 指定的线程池。传入null则会关闭默认线程池。
     * @deprecated 没啥用的方法，要关闭线程池还不如直接调用线程池的关闭方法，避免歧义。
     */
    @Deprecated
    public static void shutDown(ExecutorService executorService) {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
