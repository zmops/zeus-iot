package com.zmops.zeus.iot.server.transfer.core.state;

/**
 * callbacks
 */
public interface StateCallback {

    void call(State before, State after);
}
