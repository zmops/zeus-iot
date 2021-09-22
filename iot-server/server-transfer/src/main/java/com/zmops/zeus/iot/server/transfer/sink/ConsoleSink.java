/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zmops.zeus.iot.server.transfer.sink;


import com.zmops.zeus.iot.server.transfer.api.Message;
import com.zmops.zeus.iot.server.transfer.api.Sink;
import com.zmops.zeus.iot.server.transfer.conf.JobProfile;

import java.nio.charset.StandardCharsets;

/**
 * message write to console
 */
public class ConsoleSink implements Sink {

    @Override
    public void write(Message message) {
        if (message != null) {
            System.out.println(new String(message.getBody(), StandardCharsets.UTF_8));
        }
    }

    @Override
    public void setSourceFile(String sourceFileName) {

    }

    @Override
    public void init(JobProfile jobConf) {

    }

    @Override
    public void destroy() {

    }
}
