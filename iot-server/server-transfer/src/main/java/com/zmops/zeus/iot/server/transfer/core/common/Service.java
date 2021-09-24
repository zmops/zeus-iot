package com.zmops.zeus.iot.server.transfer.core.common;

/**
 * Service lifecycle interface.
 */
public interface Service {

    /**
     * start service
     *
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * stop service
     *
     * @throws Exception
     */
    void stop() throws Exception;

    /**
     * join and wait until getting signal
     *
     * @throws Exception
     */
    void join() throws Exception;
}
