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

package com.zmops.zeus.iot.server.telemetry.api;

/**
 * The telemetry context which the metrics instances may need to know.
 */
public enum TelemetryRelatedContext {
    INSTANCE;

    private volatile String id = null;

    TelemetryRelatedContext() {
    }

    /**
     * Set a global ID to represent the current iot instance
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the iot instance ID, if be set before.
     *
     * @return id or null.
     */
    public String getId() {
        return id;
    }
}
