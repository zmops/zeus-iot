package com.zmops.zeus.iot.server.transfer.core.job;


import com.zmops.zeus.iot.server.transfer.core.metrics.Metric;
import com.zmops.zeus.iot.server.transfer.core.metrics.Metrics;
import com.zmops.zeus.iot.server.transfer.core.metrics.MetricsRegister;
import com.zmops.zeus.iot.server.transfer.core.metrics.gauge.GaugeInt;

import java.util.concurrent.atomic.AtomicBoolean;

@Metrics
public class JobMetrics {
    private static final JobMetrics    JOB_METRICS   = new JobMetrics();
    private static final AtomicBoolean REGISTER_ONCE = new AtomicBoolean(false);

    @Metric
    GaugeInt runningJobs;

    @Metric
    GaugeInt fatalJobs;

    private JobMetrics() {
    }

    static JobMetrics create() {
        if (REGISTER_ONCE.compareAndSet(false, true)) {
            MetricsRegister.register("Job", "STateSummary", null, JOB_METRICS);
        }
        return JOB_METRICS;
    }
}
