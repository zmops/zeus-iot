package com.zmops.zeus.iot.server.transfer.core.api;

/**
 * For every message, there might be validators to filter required ones
 */
public interface Validator {

    /**
     * @param messageLine
     * @return
     */
    boolean validate(String messageLine);

}
