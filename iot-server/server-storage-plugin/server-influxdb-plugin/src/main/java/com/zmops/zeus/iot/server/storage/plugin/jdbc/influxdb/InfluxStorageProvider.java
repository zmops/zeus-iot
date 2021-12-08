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

package com.zmops.zeus.iot.server.storage.plugin.jdbc.influxdb;

import com.zmops.zeus.iot.server.core.CoreModule;
import com.zmops.zeus.iot.server.core.storage.IBatchDAO;
import com.zmops.zeus.iot.server.core.storage.StorageDAO;
import com.zmops.zeus.iot.server.core.storage.StorageModule;
import com.zmops.zeus.iot.server.storage.plugin.jdbc.influxdb.dao.BatchDAO;
import com.zmops.zeus.iot.server.storage.plugin.jdbc.influxdb.dao.InfluxStorageDAO;
import com.zmops.zeus.iot.server.telemetry.TelemetryModule;
import com.zmops.zeus.iot.server.telemetry.api.HealthCheckMetrics;
import com.zmops.zeus.iot.server.telemetry.api.MetricsCreator;
import com.zmops.zeus.iot.server.telemetry.api.MetricsTag;
import com.zmops.zeus.server.library.module.*;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class InfluxStorageProvider extends ModuleProvider {
    private final InfluxStorageConfig config;
    private       InfluxClient        client;

    public InfluxStorageProvider() {
        config = new InfluxStorageConfig();
    }

    @Override
    public String name() {
        return "influxdb";
    }

    @Override
    public Class<? extends ModuleDefine> module() {
        return StorageModule.class;
    }

    @Override
    public ModuleConfig createConfigBeanIfAbsent() {
        return config;
    }

    @Override
    public void prepare() throws ServiceNotProvidedException {

        client = new InfluxClient(config);

        this.registerServiceImplementation(IBatchDAO.class, new BatchDAO(client));
        this.registerServiceImplementation(StorageDAO.class, new InfluxStorageDAO(client));

    }

    @Override
    public void start() throws ServiceNotProvidedException, ModuleStartException {
        MetricsCreator metricCreator = getManager().find(TelemetryModule.NAME).provider().getService(MetricsCreator.class);
        HealthCheckMetrics healthChecker = metricCreator.createHealthCheckerGauge(
                "storage_influxdb", MetricsTag.EMPTY_KEY, MetricsTag.EMPTY_VALUE);

        client.registerChecker(healthChecker);
        client.connect();
    }

    @Override
    public void notifyAfterCompleted() throws ServiceNotProvidedException {

    }

    @Override
    public String[] requiredModules() {
        return new String[]{CoreModule.NAME};
    }
}
