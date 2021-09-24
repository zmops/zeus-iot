package com.zmops.zeus.iot.server.transfer.core.api;

/**
 * filter for source split
 */
public interface Filter {

    /**
     * whether reader is legal.
     *
     * @return true if reader is legal else false.
     */
    boolean match();
}
