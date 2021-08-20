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

package com.zmops.zeus.iot.server.sender.provider.protocol.bean;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


public class ZabbixTrapperRequest {

    /**
     * Request type
     */
    private ZabbixProtocolType type;

    @Setter
    @Getter
    private String request = ZabbixProtocolType.SENDER_DATA.getName();

    /**
     * Agent push data
     *
     * @see ZabbixProtocolType#AGENT_DATA
     */
    @Setter
    @Getter
    private List<SenderData> data;

    @Setter
    @Getter
    private long clock = System.currentTimeMillis() / 1000L;

    @Setter
    @Getter
    private long ns = System.nanoTime() % 1_000_000_000L;

    @Data
    public static class SenderData {
        private String host;
        private String key;
        private String value;
        private long   clock;
        private long   ns;

        public SenderData() {
            this.clock = System.currentTimeMillis() / 1000L;
            this.ns = System.nanoTime() % 1_000_000_000L;
        }
    }

}
