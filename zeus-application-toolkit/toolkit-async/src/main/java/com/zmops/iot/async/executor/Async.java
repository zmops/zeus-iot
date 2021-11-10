package com.zmops.iot.async.executor;


import com.zmops.iot.async.executor.timer.SystemClock;
import com.zmops.iot.async.worker.OnceWork;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.async.wrapper.WorkerWrapperGroup;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

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

        final WorkerWrapperGroup group = new WorkerWrapperGroup(SystemClock.now(), timeout);
        final OnceWork.Impl onceWork = new OnceWork.Impl(group, workId);
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

}
