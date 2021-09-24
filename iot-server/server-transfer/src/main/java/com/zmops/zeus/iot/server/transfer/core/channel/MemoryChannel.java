package com.zmops.zeus.iot.server.transfer.core.channel;


import com.zmops.zeus.iot.server.transfer.conf.JobProfile;
import com.zmops.zeus.iot.server.transfer.conf.TransferConstants;
import com.zmops.zeus.iot.server.transfer.api.Channel;
import com.zmops.zeus.iot.server.transfer.api.Message;
import com.zmops.zeus.iot.server.transfer.metrics.PluginMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MemoryChannel implements Channel {

    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryChannel.class);

    private LinkedBlockingQueue<Message> queue;

    private final PluginMetric metric = new PluginMetric();

    /**
     * {@inheritDoc}
     */
    @Override
    public void push(Message message) {
        try {
            if (message != null) {
                metric.readNum.incr();
                queue.put(message);
                metric.readSuccessNum.incr();
            }
        } catch (InterruptedException ex) {
            metric.readFailedNum.incr();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public boolean push(Message message, long timeout, TimeUnit unit) {
        try {
            if (message != null) {
                metric.readNum.incr();
                boolean result = queue.offer(message, timeout, unit);
                if (result) {
                    metric.readSuccessNum.incr();
                } else {
                    metric.readFailedNum.incr();
                }
                return result;
            }
        } catch (InterruptedException ex) {
            metric.readFailedNum.incr();
            Thread.currentThread().interrupt();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message pull(long timeout, TimeUnit unit) {
        try {
            Message message = queue.poll(timeout, unit);
            if (message != null) {
                metric.sendSuccessNum.incr();
            }
            return message;
        } catch (InterruptedException ex) {
            metric.sendFailedNum.incr();
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void init(JobProfile jobConf) {
        queue = new LinkedBlockingQueue<>(jobConf.getInt(TransferConstants.CHANNEL_MEMORY_CAPACITY,
                TransferConstants.DEFAULT_CHANNEL_MEMORY_CAPACITY));
    }

    @Override
    public void destroy() {
        if (queue != null) {
            queue.clear();
        }
        LOGGER.info("destroy channel, memory channel metric, readNum: {}, readSuccessNum: {}, "
                        + "readFailedNum: {}, sendSuccessNum: {}, sendFailedNum: {}",
                metric.readNum.snapshot(),
                metric.readSuccessNum.snapshot(),
                metric.readFailedNum.snapshot(),
                metric.sendSuccessNum.snapshot(),
                metric.sendFailedNum.snapshot());
    }
}
