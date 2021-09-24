package com.zmops.zeus.iot.server.transfer.api;

import java.util.concurrent.TimeUnit;

/**
 * Channel is used as data buffer between source and sink.
 */
public interface Channel extends Stage {


    /**
     * write message
     *
     * @param message - message
     */
    void push(Message message);

    /**
     * write message with timeout
     *
     * @param message
     * @param timeout
     * @param unit
     * @return
     */
    boolean push(Message message, long timeout, TimeUnit unit);

    /**
     * read message with timeout
     *
     * @param timeout
     * @param unit
     * @return
     */
    Message pull(long timeout, TimeUnit unit);

}
