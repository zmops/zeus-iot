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

package com.zmops.zeus.iot.server.starter;

import com.zmops.zeus.iot.server.core.RunningMode;
import com.zmops.zeus.iot.server.starter.config.ApplicationConfigLoader;
import com.zmops.zeus.iot.server.telemetry.TelemetryModule;
import com.zmops.zeus.iot.server.telemetry.api.MetricsCreator;
import com.zmops.zeus.iot.server.telemetry.api.MetricsTag;
import com.zmops.zeus.server.library.module.ApplicationConfiguration;
import com.zmops.zeus.server.library.module.ModuleManager;
import lombok.extern.slf4j.Slf4j;

import java.util.TimeZone;


/**
 * Starter core. Load the core configuration file, and initialize the startup sequence through {@link ModuleManager}.
 * <p>
 * 模块加载启动器，动态加载模块。
 */
@Slf4j
public class IoTServerBootstrap {
    public static void start() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));

        String mode = System.getProperty("mode");
        RunningMode.setMode(mode);

        ApplicationConfigLoader configLoader = new ApplicationConfigLoader();

        ModuleManager manager = new ModuleManager();
        try {
            ApplicationConfiguration applicationConfiguration = configLoader.load();
            manager.init(applicationConfiguration);

            manager.find(TelemetryModule.NAME)
                    .provider()
                    .getService(MetricsCreator.class)
                    .createGauge("uptime", "zeus-iot server start up time", MetricsTag.EMPTY_KEY, MetricsTag.EMPTY_VALUE)
                    // Set uptime to second
                    .setValue(System.currentTimeMillis() / 1000d);

            if (RunningMode.isInitMode()) {
                log.info("Zeus IoT starts up in init mode successfully, exit now...");
                System.exit(0);
            }

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
            System.exit(1);
        }
    }
}
