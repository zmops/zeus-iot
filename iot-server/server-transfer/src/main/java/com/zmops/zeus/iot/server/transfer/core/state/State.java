package com.zmops.zeus.iot.server.transfer.core.state;

/**
 * job/task state
 */
public enum State {
    // accepted state
    ACCEPTED,
    // running
    RUNNING,
    // succeeded
    SUCCEEDED,
    // failed
    FAILED,
    // killed
    KILLED,
    // fatal after retry failed
    FATAL
}
