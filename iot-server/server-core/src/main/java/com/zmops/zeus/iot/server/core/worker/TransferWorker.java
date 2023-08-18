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

package com.zmops.zeus.iot.server.core.worker;

import com.zmops.zeus.iot.server.core.worker.data.Item;
import com.zmops.zeus.server.library.module.ModuleDefineHolder;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;

/**
 * 1. 协议入口 -> zabbix trapper item
 *
 * @param <INPUT> The type of worker input.
 */
@Slf4j
public abstract class TransferWorker<INPUT extends Item> extends AbstractWorker<INPUT> {

    TransferWorker(ModuleDefineHolder moduleDefineHolder) {
        super(moduleDefineHolder);
    }

    void onWork(List<INPUT> input) {
        prepareBatch(input);
    }

    public abstract void prepareBatch(Collection<INPUT> lastCollection);

}
