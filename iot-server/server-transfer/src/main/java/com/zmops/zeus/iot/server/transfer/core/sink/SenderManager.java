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

package com.zmops.zeus.iot.server.transfer.core.sink;


import com.zmops.zeus.iot.server.transfer.conf.JobProfile;
import com.zmops.zeus.iot.server.transfer.core.metrics.PluginMetric;
import com.zmops.zeus.iot.server.transfer.core.task.TaskPositionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * proxy client
 */
public class SenderManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SenderManager.class);

    private final TaskPositionManager taskPositionManager;
    private final String              sourceFilePath;
    private final PluginMetric        metric = new PluginMetric();

    public SenderManager(JobProfile jobConf, String bid, String sourceFilePath) {
        taskPositionManager = TaskPositionManager.getTaskPositionManager();
        this.sourceFilePath = sourceFilePath;
    }

    /**
     * Send message to proxy by batch, use message cache.
     *
     * @param bid      - bid
     * @param tid      - tid
     * @param bodyList - body list
     * @param retry    - retry time
     */
    public void sendBatch(String jobId, String bid, String tid, List<byte[]> bodyList, int retry, long dataTime) {
        try {

            bodyList.forEach(i -> {
                LOGGER.info(new String(i));
            });

            metric.sendSuccessNum.incr(bodyList.size());
            taskPositionManager.updateFileSinkPosition(jobId, sourceFilePath, bodyList.size());

            System.out.println(metric.sendSuccessNum.snapshot());

        } catch (Exception exception) {
            LOGGER.error("Exception caught", exception);
            // retry time
            try {
                TimeUnit.SECONDS.sleep(1);
                sendBatch(jobId, bid, tid, bodyList, retry + 1, dataTime);
            } catch (Exception ignored) {
                // ignore it.
            }
        }
    }
}
