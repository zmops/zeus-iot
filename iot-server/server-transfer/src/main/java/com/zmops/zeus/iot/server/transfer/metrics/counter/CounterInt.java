package com.zmops.zeus.iot.server.transfer.metrics.counter;

import java.util.concurrent.atomic.AtomicInteger;

public class CounterInt implements Counter {

    private AtomicInteger value = new AtomicInteger(0);

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
    public Integer snapshot() {
        return value.get();
    }
}
