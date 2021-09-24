package com.zmops.zeus.iot.server.transfer.core.manager;

import com.zmops.zeus.iot.server.transfer.conf.JobProfile;
import com.zmops.zeus.iot.server.transfer.conf.TransferConfiguration;
import com.zmops.zeus.iot.server.transfer.conf.TransferConstants;
import com.zmops.zeus.iot.server.transfer.conf.TriggerProfile;
import com.zmops.zeus.iot.server.transfer.core.common.AbstractDaemon;
import com.zmops.zeus.iot.server.transfer.core.db.Db;
import com.zmops.zeus.iot.server.transfer.core.db.JobProfileDb;
import com.zmops.zeus.iot.server.transfer.core.db.TriggerProfileDb;
import com.zmops.zeus.iot.server.transfer.core.job.CommandDb;
import com.zmops.zeus.iot.server.transfer.core.job.JobManager;
import com.zmops.zeus.iot.server.transfer.core.task.TaskManager;
import com.zmops.zeus.iot.server.transfer.core.task.TaskPositionManager;
import com.zmops.zeus.iot.server.transfer.core.trigger.TriggerManager;
import com.zmops.zeus.iot.server.transfer.provider.ServerTransferConfig;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.zmops.zeus.iot.server.transfer.conf.JobConstants.*;

/**
 * Zabbix History Data Transfer
 */
public class TransferManager extends AbstractDaemon {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferManager.class);

    @Getter
    private final JobManager jobManager;

    @Getter
    private final TaskManager taskManager;

    @Getter
    private final TriggerManager triggerManager;

    @Getter
    private final TaskPositionManager taskPositionManager;

    private final TransferConfiguration conf;

    @Getter
    private final Db db;

    @Getter
    private final CommandDb commandDb;

    @Getter
    private final ServerTransferConfig moduleConfig;

    public TransferManager(ServerTransferConfig config) {
        moduleConfig = config;

        conf = TransferConfiguration.getAgentConf();
        this.db = initDb();
        commandDb = new CommandDb(db);

        triggerManager = new TriggerManager(this, new TriggerProfileDb(db));
        jobManager = new JobManager(this, new JobProfileDb(db));
        taskManager = new TaskManager(this);
        taskPositionManager = TaskPositionManager.getTaskPositionManager(this);
    }

    /**
     * init db by class name
     *
     * @return db
     */
    private Db initDb() {
        try {
            // db is a required component, so if not init correctly, throw exception and stop running.
            return (Db) Class.forName(conf.get(TransferConstants.AGENT_DB_CLASSNAME, TransferConstants.DEFAULT_AGENT_DB_CLASSNAME)).newInstance();
        } catch (Exception ex) {
            throw new UnsupportedClassVersionError(ex.getMessage());
        }
    }

    @Override
    public void join() {
        super.join();
        jobManager.join();
        taskManager.join();
    }

    @Override
    public void start() throws Exception {
        LOGGER.info("starting zeus-transfer manager");

        triggerManager.start();
        jobManager.start();
        taskManager.start();
        taskPositionManager.start();

        JobProfile profile = JobProfile.parseJsonFile("job.json");
        profile.setInt(JOB_FILE_MAX_WAIT, moduleConfig.getFileMaxWait());
        profile.set(JOB_NAME, moduleConfig.getName());
        profile.set(JOB_DIR_FILTER_PATTERN, moduleConfig.getPattern());

        TriggerProfile triggerProfile = TriggerProfile.parseJobProfile(profile);
        triggerManager.addTrigger(triggerProfile);

//        jobManager.submitJobProfile(profile);
    }

    /**
     * It should guarantee thread-safe, and can be invoked many times.
     *
     * @throws Exception exceptions
     */
    @Override
    public void stop() throws Exception {

        // TODO: change job state which is in running state.
        LOGGER.info("stopping agent manager");

        // close in order: trigger -> job -> task
        triggerManager.stop();
        jobManager.stop();
        taskManager.stop();
        taskPositionManager.stop();
        this.db.close();
    }
}
