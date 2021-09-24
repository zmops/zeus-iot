package com.zmops.zeus.iot.server.transfer.core.task;

import com.zmops.zeus.iot.server.transfer.conf.JobProfile;
import com.zmops.zeus.iot.server.transfer.core.api.Channel;
import com.zmops.zeus.iot.server.transfer.core.api.Reader;
import com.zmops.zeus.iot.server.transfer.core.api.Sink;


/**
 * task meta definition which contains reader -> channel -> sink and job config information
 */
public class Task {

    private final String     taskId;
    private final Reader     reader;
    private final Sink       sink;
    private final Channel    channel;
    private final JobProfile jobConf;

    public Task(String taskId, Reader reader, Sink sink, Channel channel, JobProfile jobConf) {
        this.reader = reader;
        this.sink = sink;
        this.taskId = taskId;
        this.channel = channel;
        this.jobConf = jobConf;
    }

    public boolean isReadFinished() {
        return reader.isFinished();
    }

    public String getTaskId() {
        return taskId;
    }

    public Reader getReader() {
        return reader;
    }

    public Sink getSink() {
        return sink;
    }

    public Channel getChannel() {
        return channel;
    }

    public void init() {
        this.channel.init(jobConf);
        this.sink.init(jobConf);
        this.reader.init(jobConf);
    }

    public void destroy() {
        this.reader.destroy();
        this.sink.destroy();
        this.channel.destroy();
    }
}
