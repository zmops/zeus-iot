package com.zmops.zeus.iot.server.transfer.conf;


/**
 * Basic config for a single job
 */
public class JobConstants extends CommonConstants {

    // job id
    public static final String JOB_ID          = "job.id";
    public static final String JOB_INSTANCE_ID = "job.instance.id";
    public static final String JOB_IP          = "job.ip";
    public static final String JOB_RETRY       = "job.retry";

    public static final String JOB_TRIGGER             = "job.trigger";
    public static final String JOB_NAME                = "job.name";
    public static final String JOB_LINE_FILTER_PATTERN = "job.pattern";
    public static final String DEFAULT_JOB_NAME        = "default";
    public static final String JOB_DESCRIPTION         = "job.description";
    public static final String DEFAULT_JOB_DESCRIPTION = "default job description";
    public static final String DEFAULT_JOB_LINE_FILTER = "";

    // job type, delete/add
    public static final String JOB_TYPE = "job.type";

    public static final String JOB_CHECKPOINT = "job.checkpoint";

    // offset for time
    public static final String JOB_FILE_TIME_OFFSET = "job.timeOffset";

    public static final String DEFAULT_JOB_FILE_TIME_OFFSET = "0d";

    public static final String JOB_FILE_MAX_WAIT         = "job.file.max.wait";
    // time in min
    public static final int    DEFAULT_JOB_FILE_MAX_WAIT = 1;

    public static final String JOB_DIR_FILTER_PATTERN = "job.dir.pattern";

    public static final String JOB_DIR_FILTER_PATH = "job.dir.path";

    public static final String JOB_ID_PREFIX = "job_";

    public static final String JOB_STORE_TIME = "job.store.time";

    public static final String JOB_OP = "job.op";

    public static final String TRIGGER_ONLY_ONE_JOB = "job.standalone";

    // field splitter
    public static final String JOB_FIELD_SPLITTER = "job.splitter";

    public static final String JOB_ADDITION_STR = "job.additionStr";

    // job delivery time
    public static final String JOB_DELIVERY_TIME = "job.deliveryTime";

    // job time reading file
    public static final String JOB_DATA_TIME = "job.dataTime";

    public static final String JOB_CYCLE_UNIT = "job.cycleUnit";

    /**
     * when job is retried, the retry time should be provided
     */
    public static final String JOB_RETRY_TIME = "job.retryTime";

}
