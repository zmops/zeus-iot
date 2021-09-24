package com.zmops.zeus.iot.server.transfer.core.common;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class AgentThreadFactory implements ThreadFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentThreadFactory.class);

    private final AtomicInteger mThreadNum = new AtomicInteger(1);

    private final String threadType;

    public AgentThreadFactory(String threadType) {
        this.threadType = threadType;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, threadType + "-running-thread-" + mThreadNum.getAndIncrement());
        LOGGER.debug("{} created", t.getName());
        return t;
    }
}