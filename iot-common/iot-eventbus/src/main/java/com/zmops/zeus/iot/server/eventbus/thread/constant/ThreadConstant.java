package com.zmops.zeus.iot.server.eventbus.thread.constant;

public class ThreadConstant {
    public static final int CPUS = Math.max(2, Runtime.getRuntime().availableProcessors());

    public static final String THREAD_POOL_MULTI_MODE                = "threadpool.multi.mode";
    public static final String THREAD_POOL_SHARED_NAME               = "threadpool.shared.name";
    public static final String THREAD_POOL_NAME_CUSTOMIZED           = "threadpool.name.customized";
    public static final String THREAD_POOL_NAME_IP_SHOWN             = "threadpool.name.ipshown";
    public static final String THREAD_POOL_CORE_POOL_SIZE            = "threadpool.core.pool.size";
    public static final String THREAD_POOL_MAXIMUM_POOL_SIZE         = "threadpool.maximum.pool.size";
    public static final String THREAD_POOL_KEEP_ALIVE_TIME           = "threadpool.keep.alive.time";
    public static final String THREAD_POOL_ALLOW_CORE_THREAD_TIMEOUT = "threadpool.allow.core.thread.timeout";
    public static final String THREAD_POOL_QUEUE                     = "threadpool.queue";
    public static final String THREAD_POOL_QUEUE_CAPACITY            = "threadpool.queue.capacity";
    public static final String THREAD_POOL_REJECTED_POLICY           = "threadpool.rejected.policy";

    public static final String DEFAULT_THREADPOOL_SHARED_NAME        = "SharedThreadPool";
}