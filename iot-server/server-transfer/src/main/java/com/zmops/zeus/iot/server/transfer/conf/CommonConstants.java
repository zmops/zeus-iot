package com.zmops.zeus.iot.server.transfer.conf;

public class CommonConstants {

    public static final String PROXY_BID       = "proxy.bid";
    public static final String POSITION_SUFFIX = ".position";

    public static final String PROXY_TID = "proxy.tid";

    // max size of message list
    public static final String PROXY_PACKAGE_MAX_SIZE = "proxy.package.maxSize";

    // max size of single batch in bytes, default is 200KB.
    public static final int DEFAULT_PROXY_PACKAGE_MAX_SIZE = 2000000;

    public static final String PROXY_TID_QUEUE_MAX_NUMBER = "proxy.tid.queue.maxNumber";

    public static final int DEFAULT_PROXY_TID_QUEUE_MAX_NUMBER = 10000;

    public static final String PROXY_PACKAGE_MAX_TIMEOUT_MS = "proxy.package.maxTimeout.ms";

    public static final int DEFAULT_PROXY_PACKAGE_MAX_TIMEOUT_MS = 4 * 1000;

    public static final String PROXY_BATCH_FLUSH_INTERVAL = "proxy.batch.flush.interval";

    public static final int DEFAULT_PROXY_BATCH_FLUSH_INTERVAL = 100;

    public static final String PROXY_KEY_BID      = "bid";
    public static final String PROXY_KEY_TID      = "tid";
    public static final String PROXY_KEY_ID       = "id";
    public static final String PROXY_KEY_AGENT_IP = "agentip";
    public static final String PROXY_OCEANUS_F    = "f";
    public static final String PROXY_OCEANUS_BL   = "bl";


    public static final String FILE_MAX_NUM = "file.max.num";

    public static final int DEFAULT_FILE_MAX_NUM = 4096;

    public static final String TRIGGER_ID_PREFIX = "trigger_";

    public static final String COMMAND_STORE_INSTANCE_NAME = "commandStore";

}
