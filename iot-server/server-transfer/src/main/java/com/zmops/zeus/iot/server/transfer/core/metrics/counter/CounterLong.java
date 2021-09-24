package com.zmops.zeus.iot.server.transfer.core.metrics.counter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * atomic long for counter
 */
public class CounterLong implements Counter {

    private final AtomicLong value = new AtomicLong();

    @Override
    public void incr() {
        value.incrementAndGet();
    }

    @Override
    public void incr(int delta) {
        assert delta > 0;
        value.getAndAdd(delta);
    }

    @Override
    public Long snapshot() {
        return value.get();
    }
}
