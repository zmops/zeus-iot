package com.zmops.iot.async.util.timer;

/**
 * @author create by TcSnZh on 2021/5/12-下午6:36
 */
public abstract class AbstractWheelTimer implements Timer, AutoCloseable {
    public static final int WORKER_STATE_INIT     = 0;
    public static final int WORKER_STATE_STARTED  = 1;
    public static final int WORKER_STATE_SHUTDOWN = 2;

    public abstract void start();

    @SuppressWarnings("RedundantThrows")
    @Override
    public void close() throws Exception {
        stop();
    }
}
