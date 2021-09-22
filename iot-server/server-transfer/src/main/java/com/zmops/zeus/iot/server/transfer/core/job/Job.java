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

package com.zmops.zeus.iot.server.transfer.core.job;


import com.zmops.zeus.iot.server.transfer.conf.JobConstants;
import com.zmops.zeus.iot.server.transfer.conf.JobProfile;
import com.zmops.zeus.iot.server.transfer.core.api.Channel;
import com.zmops.zeus.iot.server.transfer.core.api.Reader;
import com.zmops.zeus.iot.server.transfer.core.api.Sink;
import com.zmops.zeus.iot.server.transfer.core.api.Source;
import com.zmops.zeus.iot.server.transfer.core.task.Task;
import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * job meta definition, job will be split into several tasks.
 */
public class Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(Job.class);

    private final JobProfile jobConf;

    // job name
    @Getter
    @Setter
    private String name;

    // job description
    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String jobInstanceId;

    public Job(JobProfile jobConf) {
        this.jobConf = jobConf;
        this.name = jobConf.get(JobConstants.JOB_NAME, JobConstants.DEFAULT_JOB_NAME);
        this.description = jobConf.get(JobConstants.JOB_DESCRIPTION, JobConstants.DEFAULT_JOB_DESCRIPTION);
        this.jobInstanceId = jobConf.get(JobConstants.JOB_INSTANCE_ID);
    }

    public List<Task> createTasks() {
        List<Task> taskList = new ArrayList<>();
        int        index    = 0;
        try {
            LOGGER.info("job id: {}, source: {}, channel: {}, sink: {}",
                    getJobInstanceId(), jobConf.get(JobConstants.JOB_SOURCE),
                    jobConf.get(JobConstants.JOB_CHANNEL),
                    jobConf.get(JobConstants.JOB_SINK));

            Source source = (Source) Class.forName(jobConf.get(JobConstants.JOB_SOURCE)).newInstance();

            for (Reader reader : source.split(jobConf)) {
                Sink writer = (Sink) Class.forName(jobConf.get(JobConstants.JOB_SINK)).newInstance();
                writer.setSourceFile(reader.getReadFile());

                Channel channel = (Channel) Class.forName(jobConf.get(JobConstants.JOB_CHANNEL)).newInstance();

                String taskId = String.format("%s_%d", jobInstanceId, index++);
                taskList.add(new Task(taskId, reader, writer, channel, getJobConf()));
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return taskList;
    }

    public JobProfile getJobConf() {
        return this.jobConf;
    }

}
