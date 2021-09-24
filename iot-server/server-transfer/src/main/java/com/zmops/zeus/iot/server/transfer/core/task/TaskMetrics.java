package com.zmops.zeus.iot.server.transfer.core.task;

import com.zmops.zeus.iot.server.transfer.core.metrics.Metric;
import com.zmops.zeus.iot.server.transfer.core.metrics.Metrics;
import com.zmops.zeus.iot.server.transfer.core.metrics.MetricsRegister;
import com.zmops.zeus.iot.server.transfer.core.metrics.counter.CounterLong;
import com.zmops.zeus.iot.server.transfer.core.metrics.gauge.GaugeInt;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Metric collector for task level.
 */
@Metrics
public class TaskMetrics {

    private static final TaskMetrics   TASK_METRICS  = new TaskMetrics();
    private static final AtomicBoolean REGISTER_ONCE = new AtomicBoolean(false);

    @Metric
    GaugeInt runningTasks;

    @Metric
    GaugeInt retryingTasks;

    @Metric
    CounterLong fatalTasks;

    private TaskMetrics() {
    }

    public static TaskMetrics create() {
        // register one time.
        if (REGISTER_ONCE.compareAndSet(false, true)) {
            MetricsRegister.register("Task", "StateSummary", null, TASK_METRICS);
        }
        return TASK_METRICS;
    }
}
