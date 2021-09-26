package com.zmops.zeus.iot.server.transfer.sender;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.zmops.zeus.iot.server.core.analysis.manual.history.History;
import com.zmops.zeus.iot.server.core.analysis.manual.history.UIntHistory;
import com.zmops.zeus.iot.server.core.analysis.record.Record;
import com.zmops.zeus.iot.server.core.analysis.worker.RecordStreamProcessor;
import com.zmops.zeus.iot.server.transfer.conf.JobProfile;
import com.zmops.zeus.iot.server.transfer.core.task.TaskPositionManager;
import com.zmops.zeus.iot.server.transfer.metrics.PluginMetric;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
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
     * @param bid      - bid 方便以后企业版 区分数据来源
     * @param tid      - tid
     * @param bodyList - body list
     * @param retry    - retry time
     */
    public void sendBatch(String jobId, String bid, String tid, List<byte[]> bodyList, int retry, long dataTime) {
        try {
            bodyList.forEach(body -> {
                ItemValue itemValue = JSON.parseObject(new String(body, StandardCharsets.UTF_8), ItemValue.class);

                Record record;

                if (itemValue.getType() == 3) { //uint
                    record = new UIntHistory();
                } else if (itemValue.getType() == 0) {
                    record = new History();
                } else {
                    return;
                }

                record.setItemid(itemValue.itemid);
                record.setValue(itemValue.getHost().get("host"), itemValue.value, itemValue.getTimestamp());

                RecordStreamProcessor.getInstance().in(record);
            });

            metric.sendSuccessNum.incr(bodyList.size());
            taskPositionManager.updateFileSinkPosition(jobId, sourceFilePath, bodyList.size());

            LOGGER.info("send bid {} with message size {}, the job id is {},the tid is {}, read file is {}"
                    + "dataTime is {}", bid, bodyList.size(), jobId, tid, sourceFilePath, dataTime);

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

    @Setter
    @Getter
    static class ItemValue {

        private Integer type;

        private String name;

        private Long clock;

        private Integer itemid;

        private List<Map<String, String>> item_tags;

        private List<String> groups;

        private Long ns;

        private String value;

        private Map<String, String> host;

        public Long getTimestamp() {
            return Long.valueOf(clock + String.format("%09d", ns).substring(0, 3));
        }
    }
}
