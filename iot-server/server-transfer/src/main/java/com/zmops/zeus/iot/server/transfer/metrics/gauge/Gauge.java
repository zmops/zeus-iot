package com.zmops.zeus.iot.server.transfer.metrics.gauge;

import com.zmops.zeus.iot.server.transfer.metrics.MutableMetric;

public interface Gauge<T> extends MutableMetric {

    void set(T num);

    /**
     * +1
     */
    void incr();

    /**
     * + delt
     *
     * @param delta > 0
     */
    void incr(int delta);

    /**
     * -1
     */
    void decr();

    /**
     * -delta
     *
     * @param delta > 0
     */
    void decr(int delta);
}
