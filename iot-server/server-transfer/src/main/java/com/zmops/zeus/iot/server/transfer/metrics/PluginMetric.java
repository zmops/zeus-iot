package com.zmops.zeus.iot.server.transfer.metrics;

import com.zmops.zeus.iot.server.transfer.metrics.counter.CounterLong;

/**
 * Common plugin metrics
 */
@Metrics
public class PluginMetric {

    @Metric
    public Tag tagName;

    @Metric
    public CounterLong readNum;

    @Metric
    public CounterLong sendNum;

    @Metric
    public CounterLong sendFailedNum;

    @Metric
    public CounterLong readFailedNum;

    @Metric
    public CounterLong readSuccessNum;

    @Metric
    public CounterLong sendSuccessNum;

    public PluginMetric() {
        // every metric should register, otherwise not working.
        MetricsRegister.register("Plugin", "PluginSummary", null, this);
    }
}
