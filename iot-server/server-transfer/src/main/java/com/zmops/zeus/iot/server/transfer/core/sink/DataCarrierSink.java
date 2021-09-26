package com.zmops.zeus.iot.server.transfer.core.sink;

import com.zmops.zeus.iot.server.transfer.api.Message;
import com.zmops.zeus.iot.server.transfer.api.Sink;
import com.zmops.zeus.iot.server.transfer.common.TransferThreadFactory;
import com.zmops.zeus.iot.server.transfer.conf.CommonConstants;
import com.zmops.zeus.iot.server.transfer.conf.JobProfile;
import com.zmops.zeus.iot.server.transfer.core.message.EndMessage;
import com.zmops.zeus.iot.server.transfer.core.message.PackProxyMessage;
import com.zmops.zeus.iot.server.transfer.core.message.ProxyMessage;
import com.zmops.zeus.iot.server.transfer.sender.SenderManager;
import com.zmops.zeus.iot.server.transfer.utils.TransferUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.zmops.zeus.iot.server.transfer.conf.CommonConstants.*;
import static com.zmops.zeus.iot.server.transfer.conf.JobConstants.*;


public class DataCarrierSink implements Sink {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataCarrierSink.class);

    private SenderManager senderManager;

    private String bid;
    private String tid;
    private String sourceFile;
    private String jobInstanceId;

    private int maxBatchSize;
    private int maxBatchTimeoutMs;
    private int batchFlushInterval;
    private int maxQueueNumber;

    private final ExecutorService executorService =
            new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new TransferThreadFactory("ProxySink"));

    private volatile boolean shutdown = false;

    // key is tid, value is a batch of messages belong to the same tid
    private ConcurrentHashMap<String, PackProxyMessage> cache;

    private long dataTime;


    @Override
    public void write(Message message) {
        if (message != null && message.getBody().length > 0) {
            message.getHeader().put(CommonConstants.PROXY_KEY_BID, bid);
            message.getHeader().put(CommonConstants.PROXY_KEY_TID, tid);
            if (!(message instanceof EndMessage)) {

                ProxyMessage proxyMessage = ProxyMessage.parse(message);

                // add proxy message to cache.
                cache.compute(proxyMessage.getTid(), (s, packProxyMessage) -> {
                    if (packProxyMessage == null) {
                        packProxyMessage = new PackProxyMessage(maxBatchSize, maxQueueNumber, maxBatchTimeoutMs, proxyMessage.getTid());
                    }
                    // add message to package proxy
                    packProxyMessage.addProxyMessage(proxyMessage);
                    return packProxyMessage;
                });
            }
        }
    }

    @Override
    public void setSourceFile(String sourceFileName) {
        this.sourceFile = sourceFileName;
    }

    /**
     * flush cache by batch
     *
     * @return - thread runner
     */
    private Runnable flushCache() {
        return () -> {
            LOGGER.info("start flush cache thread for {} TDBusSink", bid);
            while (!shutdown) {
                try {
                    cache.forEach((s, packProxyMessage) -> {

                        Pair<String, List<byte[]>> result = packProxyMessage.fetchBatch();

                        if (result != null) {
                            senderManager.sendBatch(jobInstanceId, bid, result.getKey(), result.getValue(), 0, dataTime);

                            LOGGER.info("send bid {} with message size {}, the job id is {}, read file is {}"
                                    + "dataTime is {}", bid, result.getRight().size(), jobInstanceId, sourceFile, dataTime);
                        }
                    });
                    TransferUtils.silenceSleepInMs(batchFlushInterval);
                } catch (Exception ex) {
                    LOGGER.error("error caught", ex);
                }
            }
        };
    }

    @Override
    public void init(JobProfile jobConf) {
        maxBatchSize = jobConf.getInt(CommonConstants.PROXY_PACKAGE_MAX_SIZE, CommonConstants.DEFAULT_PROXY_PACKAGE_MAX_SIZE);
        maxQueueNumber = jobConf.getInt(CommonConstants.PROXY_TID_QUEUE_MAX_NUMBER, CommonConstants.DEFAULT_PROXY_TID_QUEUE_MAX_NUMBER);
        maxBatchTimeoutMs = jobConf.getInt(CommonConstants.PROXY_PACKAGE_MAX_TIMEOUT_MS, CommonConstants.DEFAULT_PROXY_PACKAGE_MAX_TIMEOUT_MS);

        jobInstanceId = jobConf.get(JOB_INSTANCE_ID);

        batchFlushInterval = jobConf.getInt(CommonConstants.PROXY_BATCH_FLUSH_INTERVAL, CommonConstants.DEFAULT_PROXY_BATCH_FLUSH_INTERVAL);

        cache = new ConcurrentHashMap<>(10);

        bid = jobConf.get(PROXY_BID);
        dataTime = TransferUtils.timeStrConvertToMillSec(jobConf.get(JOB_DATA_TIME, ""), jobConf.get(JOB_CYCLE_UNIT, ""));

        bid = jobConf.get(PROXY_BID);
        tid = jobConf.get(PROXY_TID);

        executorService.execute(flushCache());
        senderManager = new SenderManager(jobConf, bid, sourceFile);
    }

    private HashMap<String, String> parseAttrFromJobProfile(JobProfile jobProfile) {
        HashMap<String, String> attr = new HashMap<>();

        String additionStr = jobProfile.get(JOB_ADDITION_STR, "");
        if (!additionStr.isEmpty()) {
            Map<String, String> addAttr = TransferUtils.getAdditionAttr(additionStr);
            attr.putAll(addAttr);
        }

        if (jobProfile.getBoolean(JOB_RETRY, false)) {
            // used for online compute filter consume
            attr.put(PROXY_OCEANUS_F, PROXY_OCEANUS_BL);
        }
        attr.put(PROXY_KEY_ID, jobProfile.get(JOB_ID));
        attr.put(PROXY_KEY_AGENT_IP, jobProfile.get(JOB_IP));

        return attr;
    }

    @Override
    public void destroy() {
        LOGGER.info("destroy sink which sink from source file {}", sourceFile);
        while (!sinkFinish()) {
            LOGGER.info("job {} wait until cache all flushed to proxy", jobInstanceId);
            TransferUtils.silenceSleepInMs(batchFlushInterval);
        }
        shutdown = true;
        executorService.shutdown();
    }

    /**
     * check whether all tid messages finished
     *
     * @return
     */
    private boolean sinkFinish() {
        return cache.values().stream().allMatch(PackProxyMessage::isEmpty);
    }
}
