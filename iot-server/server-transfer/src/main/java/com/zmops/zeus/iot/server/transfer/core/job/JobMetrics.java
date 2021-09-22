/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
