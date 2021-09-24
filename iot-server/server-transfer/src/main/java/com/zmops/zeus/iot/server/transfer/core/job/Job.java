package com.zmops.zeus.iot.server.transfer.core.job;


import com.zmops.zeus.iot.server.transfer.conf.JobConstants;
import com.zmops.zeus.iot.server.transfer.conf.JobProfile;
import com.zmops.zeus.iot.server.transfer.api.Channel;
import com.zmops.zeus.iot.server.transfer.api.Reader;
import com.zmops.zeus.iot.server.transfer.api.Sink;
import com.zmops.zeus.iot.server.transfer.api.Source;
import com.zmops.zeus.iot.server.transfer.core.channel.MemoryChannel;
import com.zmops.zeus.iot.server.transfer.core.sink.NdjsonSink;
import com.zmops.zeus.iot.server.transfer.core.source.TextFileSource;
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

    @Getter
    @Setter
    private String name;

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
            LOGGER.info("job id: {}, job name {} ", getJobInstanceId(), getName());

            Source source = new TextFileSource();

            for (Reader reader : source.split(jobConf)) {
                Sink writer = new NdjsonSink();
                writer.setSourceFile(reader.getReadFile());

                Channel channel = new MemoryChannel();

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
