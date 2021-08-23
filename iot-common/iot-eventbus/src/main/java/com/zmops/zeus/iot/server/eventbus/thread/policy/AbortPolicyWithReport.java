package com.zmops.zeus.iot.server.eventbus.thread.policy;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

// 任务饱和时, 抛弃任务，抛出异常
@Slf4j
public class AbortPolicyWithReport extends ThreadPoolExecutor.AbortPolicy {


    private final String threadName;

    public AbortPolicyWithReport() {
        this(null);
    }

    public AbortPolicyWithReport(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        if (threadName != null) {
            log.error("Thread pool [{}] is exhausted, executor={}", threadName, executor.toString());
        }

        super.rejectedExecution(runnable, executor);
    }
}