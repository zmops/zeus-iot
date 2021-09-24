package com.zmops.zeus.iot.server.transfer.metrics;

public interface MetricSnapshot<T> {
    T snapshot();
}
