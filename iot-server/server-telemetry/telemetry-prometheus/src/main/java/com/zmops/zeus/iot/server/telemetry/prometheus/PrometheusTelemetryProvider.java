/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.zmops.zeus.iot.server.telemetry.prometheus;

import com.zmops.zeus.iot.server.telemetry.TelemetryModule;
import com.zmops.zeus.iot.server.telemetry.api.MetricsCollector;
import com.zmops.zeus.iot.server.telemetry.api.MetricsCreator;
import com.zmops.zeus.iot.server.telemetry.prometheus.httpserver.HttpServer;
import com.zmops.zeus.server.library.module.*;
import io.prometheus.client.hotspot.DefaultExports;

/**
 * Start the Prometheus
 */
public class PrometheusTelemetryProvider extends ModuleProvider {
    private PrometheusConfig config;

    public PrometheusTelemetryProvider() {
        config = new PrometheusConfig();
    }

    @Override
    public String name() {
        return "prometheus";
    }

    @Override
    public Class<? extends ModuleDefine> module() {
        return TelemetryModule.class;
    }

    @Override
    public ModuleConfig createConfigBeanIfAbsent() {
        return config;
    }

    @Override
    public void prepare() throws ServiceNotProvidedException, ModuleStartException {
        this.registerServiceImplementation(MetricsCreator.class, new PrometheusMetricsCreator());
        this.registerServiceImplementation(MetricsCollector.class, new PrometheusMetricsCollector());
        try {
            new HttpServer(config).start();
        } catch (InterruptedException e) {
            throw new ModuleStartException(e.getMessage(), e);
        }

        DefaultExports.initialize();
    }

    @Override
    public void start() throws ServiceNotProvidedException, ModuleStartException {

    }

    @Override
    public void notifyAfterCompleted() throws ServiceNotProvidedException, ModuleStartException {
    }

    @Override
    public String[] requiredModules() {
        return new String[0];
    }
}
