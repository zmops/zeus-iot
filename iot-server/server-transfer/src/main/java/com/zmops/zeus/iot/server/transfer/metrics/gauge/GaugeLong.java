package com.zmops.zeus.iot.server.transfer.metrics.gauge;

import java.util.concurrent.atomic.AtomicLong;

public class GaugeLong implements Gauge<Long> {

    private final AtomicLong value = new AtomicLong(0);

    @Override
    public void set(Long num) {
        value.set(num);
    }

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
    public void decr() {
        value.decrementAndGet();
    }

    @Override
    public void decr(int delta) {
        assert delta > 0;
        value.getAndAdd(-delta);
    }

    @Override
    public Long snapshot() {
        return value.get();
    }
}
