package com.zmops.zeus.iot.server.transfer.metrics.counter;


import com.zmops.zeus.iot.server.transfer.metrics.MutableMetric;

public interface Counter extends MutableMetric {

    /**
     * +1
     */
    void incr();

    /**
     * +delta
     *
     * @param delta > 0
     */
    void incr(int delta);

}
