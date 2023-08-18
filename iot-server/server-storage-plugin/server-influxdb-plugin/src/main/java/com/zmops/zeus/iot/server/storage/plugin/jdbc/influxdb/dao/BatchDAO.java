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

import com.zmops.zeus.iot.server.client.request.InsertRequest;
import com.zmops.zeus.iot.server.client.request.PrepareRequest;
import com.zmops.zeus.iot.server.core.storage.IBatchDAO;
import com.zmops.zeus.iot.server.storage.plugin.jdbc.influxdb.InfluxClient;
import com.zmops.zeus.server.library.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.dto.BatchPoints;

import java.util.List;

@Slf4j
public class BatchDAO implements IBatchDAO {
    private final InfluxClient client;

    public BatchDAO(InfluxClient client) {
        this.client = client;
    }

    @Override
    public void insert(InsertRequest insertRequest) {
        client.write(((InfluxInsertRequest) insertRequest).getPoint());
    }

    @Override
    public void flush(List<PrepareRequest> prepareRequests) {
        if (CollectionUtils.isEmpty(prepareRequests)) {
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("batch sql statements execute, data size: {}", prepareRequests.size());
        }

        final BatchPoints.Builder builder = BatchPoints.builder();
        prepareRequests.forEach(e -> {
            builder.point(((InfluxInsertRequest) e).getPoint());
        });

        client.write(builder.build());
    }
}
