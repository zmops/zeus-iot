package com.zmops.zeus.iot.server.transfer.conf;

public class TransferConstants {

    public static final String AGENT_HOME         = "agent.home";
    public static final String DEFAULT_AGENT_HOME = System.getProperty("agent.home");


    public static final String AGENT_LOCAL_STORE_PATH         = "agent.localStore.path";
    public static final String DEFAULT_AGENT_LOCAL_STORE_PATH = ".bdb";

    public static final String AGENT_DB_INSTANCE_NAME         = "agent.db.instance.name";
    public static final String DEFAULT_AGENT_DB_INSTANCE_NAME = "agent";

    public static final String AGENT_DB_CLASSNAME         = "agent.db.classname";
    public static final String DEFAULT_AGENT_DB_CLASSNAME = "com.zmops.zeus.iot.server.transfer.core.db.BerkeleyDbImp";

    public static final String AGENT_CONF_PARENT         = "agent.conf.parent";
    public static final String DEFAULT_AGENT_CONF_PARENT = "conf";

    public static final String  AGENT_LOCAL_STORE_READONLY         = "agent.localStore.readonly";
    public static final boolean DEFAULT_AGENT_LOCAL_STORE_READONLY = false;

    public static final String TRIGGER_FETCH_INTERVAL         = "trigger.fetch.interval";
    public static final int    DEFAULT_TRIGGER_FETCH_INTERVAL = 1;

    public static final String TRIGGER_MAX_RUNNING_NUM         = "trigger.max.running.num";
    public static final int    DEFAULT_TRIGGER_MAX_RUNNING_NUM = 4096;

    public static final String  AGENT_LOCAL_STORE_TRANSACTIONAL         = "agent.localStore.transactional";
    public static final boolean DEFAULT_AGENT_LOCAL_STORE_TRANSACTIONAL = true;

    public static final String AGENT_LOCAL_STORE_LOCK_TIMEOUT         = "agent.localStore.lockTimeout";
    public static final int    DEFAULT_AGENT_LOCAL_STORE_LOCK_TIMEOUT = 10000;

    public static final String  AGENT_LOCAL_STORE_NO_SYNC_VOID         = "agent.localStore.noSyncVoid";
    public static final boolean DEFAULT_AGENT_LOCAL_STORE_NO_SYNC_VOID = false;

    public static final String  AGENT_LOCAL_STORE_WRITE_NO_SYNC_VOID         = "agent.localStore.WriteNoSyncVoid";
    public static final boolean DEFAULT_AGENT_LOCAL_STORE_WRITE_NO_SYNC_VOID = false;


    public static final String THREAD_POOL_AWAIT_TIME         = "thread.pool.await.time";
    // time in ms
    public static final long   DEFAULT_THREAD_POOL_AWAIT_TIME = 300;


    public static final String JOB_MONITOR_INTERVAL         = "job.monitor.interval";
    public static final int    DEFAULT_JOB_MONITOR_INTERVAL = 5;


    public static final String JOB_FINISH_CHECK_INTERVAL         = "job.finish.checkInterval";
    public static final long   DEFAULT_JOB_FINISH_CHECK_INTERVAL = 6L;

    public static final String TASK_RETRY_MAX_CAPACITY         = "task.retry.maxCapacity";
    public static final int    DEFAULT_TASK_RETRY_MAX_CAPACITY = 10000;

    public static final String TASK_MONITOR_INTERVAL         = "task.monitor.interval";
    public static final int    DEFAULT_TASK_MONITOR_INTERVAL = 6;

    public static final String TASK_RETRY_SUBMIT_WAIT_SECONDS         = "task.retry.submit.waitSeconds";
    public static final int    DEFAULT_TASK_RETRY_SUBMIT_WAIT_SECONDS = 5;

    public static final String TASK_MAX_RETRY_TIME         = "task.maxRetry.time";
    public static final int    DEFAULT_TASK_MAX_RETRY_TIME = 3;

    public static final String TASK_PUSH_MAX_SECOND         = "task.push.maxSecond";
    public static final int    DEFAULT_TASK_PUSH_MAX_SECOND = 2;

    public static final String TASK_PULL_MAX_SECOND         = "task.pull.maxSecond";
    public static final int    DEFAULT_TASK_PULL_MAX_SECOND = 2;

    public static final String CHANNEL_MEMORY_CAPACITY         = "channel.memory.capacity";
    public static final int    DEFAULT_CHANNEL_MEMORY_CAPACITY = 10000;

    public static final String TRIGGER_CHECK_INTERVAL         = "trigger.check.interval";
    public static final int    DEFAULT_TRIGGER_CHECK_INTERVAL = 2;

    public static final String JOB_DB_CACHE_TIME         = "job.db.cache.time";
    // cache for 3 days.
    public static final long   DEFAULT_JOB_DB_CACHE_TIME = 3 * 24 * 60 * 60 * 1000;

    public static final String JOB_DB_CACHE_CHECK_INTERVAL         = "job.db.cache.check.interval";
    public static final int    DEFAULT_JOB_DB_CACHE_CHECK_INTERVAL = 60 * 60;


}
