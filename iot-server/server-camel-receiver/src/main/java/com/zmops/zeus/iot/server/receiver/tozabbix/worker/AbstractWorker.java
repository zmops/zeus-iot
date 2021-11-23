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

package com.zmops.zeus.iot.server.receiver.tozabbix.worker;

import com.zmops.zeus.server.library.module.ModuleDefineHolder;
import lombok.Getter;


/**
 * Abstract worker definition. Provide the {@link ModuleDefineHolder} to make sure the worker could find and access
 * services in different modules. Also, {@link #in(Object)} is provided as the primary entrance of every worker.
 * <p>
 *
 * @param <INPUT> the datatype this worker implementation processes.
 * @editor nantian
 * 数据从协议入口 发送到 zabbix 过程中的队列，to("zabbix")
 */
public abstract class AbstractWorker<INPUT> {

    @Getter
    private final ModuleDefineHolder moduleDefineHolder;

    public AbstractWorker(ModuleDefineHolder moduleDefineHolder) {
        this.moduleDefineHolder = moduleDefineHolder;
    }

    /**
     * Main entrance of this worker.
     */
    public abstract void in(INPUT input);
}
