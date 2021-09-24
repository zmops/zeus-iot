package com.zmops.zeus.iot.server.transfer.metrics;

public class MetricException extends RuntimeException {

    public MetricException(String message, Exception ex) {
        super(message, ex);
    }

    public MetricException(String message) {
        super(message);
    }
}
