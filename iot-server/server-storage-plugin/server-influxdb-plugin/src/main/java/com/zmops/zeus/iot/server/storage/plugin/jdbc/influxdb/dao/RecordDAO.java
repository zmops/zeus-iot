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
import com.zmops.zeus.iot.server.core.analysis.record.Record;
import com.zmops.zeus.iot.server.core.storage.IRecordDAO;
import com.zmops.zeus.iot.server.core.storage.model.Model;
import com.zmops.zeus.server.datacarrier.common.AtomicRangeInteger;


public class RecordDAO implements IRecordDAO {
    private static final int                PADDING_SIZE = 1_000_000;
    private static final AtomicRangeInteger SUFFIX       = new AtomicRangeInteger(0, PADDING_SIZE);

    public RecordDAO() {

    }

    @Override
    public InsertRequest prepareBatchInsert(Model model, Record record) {

        final InfluxInsertRequest request = new InfluxInsertRequest(model, record);

        return request;
    }


}
