package com.zmops.zeus.iot.server.transfer.metrics.gauge;

import java.util.concurrent.atomic.AtomicInteger;

public class GaugeInt implements Gauge<Integer> {

    private final AtomicInteger value = new AtomicInteger(0);

    @Override
    public void set(Integer num) {
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
    public Integer snapshot() {
        return value.get();
    }
}
