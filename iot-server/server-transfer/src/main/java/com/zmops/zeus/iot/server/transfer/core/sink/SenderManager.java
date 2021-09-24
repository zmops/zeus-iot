package com.zmops.zeus.iot.server.transfer.core.sink;

import com.zmops.zeus.iot.server.transfer.conf.JobProfile;
import com.zmops.zeus.iot.server.transfer.core.metrics.PluginMetric;
import com.zmops.zeus.iot.server.transfer.core.task.TaskPositionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 发送管理
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
//          RecordStreamProcessor.getInstance().in(record); //TODO

            metric.sendSuccessNum.incr(bodyList.size());
            taskPositionManager.updateFileSinkPosition(jobId, sourceFilePath, bodyList.size());

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
