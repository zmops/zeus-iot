package com.zmops.zeus.iot.server.transfer.core.metrics;

public interface MetricSnapshot<T> {
    T snapshot();
}
