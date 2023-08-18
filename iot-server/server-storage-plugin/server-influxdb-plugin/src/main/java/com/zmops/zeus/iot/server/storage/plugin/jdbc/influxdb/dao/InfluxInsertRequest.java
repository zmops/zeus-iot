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

package com.zmops.zeus.iot.server.storage.plugin.jdbc.influxdb.dao;

import com.google.common.collect.Maps;
import com.zmops.zeus.iot.server.client.request.InsertRequest;
import com.zmops.zeus.iot.server.core.analysis.manual.history.History;
import com.zmops.zeus.iot.server.core.analysis.manual.history.UIntHistory;
import com.zmops.zeus.iot.server.core.storage.StorageData;
import com.zmops.zeus.iot.server.core.storage.model.Model;
import org.influxdb.dto.Point;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * InfluxDB Point wrapper.
 */
public class InfluxInsertRequest implements InsertRequest {
    private final Point.Builder       builder;
    private final Map<String, Object> fields = Maps.newHashMap();

    public <T extends StorageData> InfluxInsertRequest(Model model, T storageData) {
        String  deviceid = "";
        Integer itemid   = 0;
        String  value    = "";
        Long    time     = 0L;

        if (model.getName().equals("history")) {

            History history = (History) storageData;
            deviceid = history.getDeviceId();
            itemid = history.getItemid();
            value = history.getValue();
            time = history.getClock();
            fields.put("value", Double.parseDouble(value));
        } else {
            UIntHistory uihistory = (UIntHistory) storageData;
            deviceid = uihistory.getDeviceId();
            itemid = uihistory.getItemid();
            value = uihistory.getValue();
            time = uihistory.getClock();
            fields.put("value", Long.parseLong(value));
        }

        builder = Point.measurement("h_" + itemid)
                .fields(fields);
        builder.tag("deviceid", deviceid);
        builder.tag("itemid", itemid + "");
        builder.time(time, TimeUnit.NANOSECONDS);
    }

    public InfluxInsertRequest time(long time, TimeUnit unit) {
        builder.time(time, unit);
        return this;
    }

    public Point getPoint() {
        return builder.build();
    }
}