package com.zmops.zeus.iot.server.eventbus.thread.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

// 使用阻塞生产者的饱和策略，不抛弃任务，也不抛出异常，当队列满时改为调用BlockingQueue.put来实现生产者的阻塞
public class BlockingPolicyWithReport implements RejectedExecutionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(BlockingPolicyWithReport.class);

    private final String threadName;

    public BlockingPolicyWithReport() {
        this(null);
    }

    public BlockingPolicyWithReport(String threadName) {
        this.threadName = threadName;
    }

    // 队列用法：
    // 1.add      增加一个元素，如果队列已满，则抛出一个IIIegaISlabEepeplian异常
    // 2.remove   移除并返回队列头部的元素，如果队列为空，则抛出一个NoSuchElementException异常
    // 3.element  返回队列头部的元素，如果队列为空，则抛出一个NoSuchElementException异常
    // 4.offer    添加一个元素并返回true，如果队列已满，则返回false
    // 5.poll     移除并返问队列头部的元素，如果队列为空，则返回null
    // 6.peek     返回队列头部的元，如果队列为空，则返回null
    // 7.put      添加一个元素，如果队列已满，则阻塞
    // 8.take     移除并返回队列头部的元素，如果队列为空，则阻塞
    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        if (threadName != null) {
            LOG.error("Thread pool [{}] is exhausted, executor={}", threadName, executor.toString());
        }

        if (!executor.isShutdown()) {
            try {
                // 添加一个元素， 如果队列满，则阻塞
                executor.getQueue().put(runnable);
            } catch (InterruptedException e) {
                // should not be interrupted
            }
        }
    }
}