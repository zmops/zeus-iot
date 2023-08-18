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

package com.zmops.zeus.iot.server.health.checker.module;

import com.zmops.zeus.iot.server.health.checker.provider.HealthQueryService;
import com.zmops.zeus.server.library.module.ModuleDefine;

/**
 * HealthCheckerModule intends to provide a channel to expose the healthy status of modules to external.
 */
public class HealthCheckerModule extends ModuleDefine {
    public static final String NAME = "health-checker";

    public HealthCheckerModule() {
        super(NAME);
    }

    @Override
    public Class[] services() {
        return new Class[]{HealthQueryService.class};
    }
}
