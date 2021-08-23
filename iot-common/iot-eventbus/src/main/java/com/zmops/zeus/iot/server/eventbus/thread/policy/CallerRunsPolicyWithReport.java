package com.zmops.zeus.iot.server.eventbus.thread.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

// 使用Caller-Runs(调用者执行)饱和策略，不抛弃任务，也不抛出异常，而是将当前任务回退到发起这个调用者执行的线程所在的上级线程去执行
public class CallerRunsPolicyWithReport extends ThreadPoolExecutor.CallerRunsPolicy {
    private static final Logger LOG = LoggerFactory.getLogger(CallerRunsPolicyWithReport.class);

    private final String threadName;

    public CallerRunsPolicyWithReport() {
        this(null);
    }

    public CallerRunsPolicyWithReport(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        if (threadName != null) {
            LOG.error("Thread pool [{}] is exhausted, executor={}", threadName, executor.toString());
        }

        super.rejectedExecution(runnable, executor);
    }
}