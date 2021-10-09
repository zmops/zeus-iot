package com.zmops.zeus.iot.server.transfer.core.job;


import com.zmops.zeus.iot.server.transfer.conf.TriggerProfile;
import com.zmops.zeus.iot.server.transfer.core.db.Db;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CommandDb {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandDb.class);

    public static final int MANAGER_SUCCESS_CODE = 0;
    public static final int MANAGER_FAIL_CODE = 1;

    private final Db db;

    public CommandDb(Db db) {
        this.db = db;
    }

    /**
     * store manager command to db
     *
     * @param commandEntity
     */
    public void storeCommand(CommandEntity commandEntity) {
        db.putCommand(commandEntity);
    }

    /**
     * get those commands not ack to manager
     *
     * @return
     */
    public List<CommandEntity> getUnackedCommands() {
        return db.searchCommands(false);
    }


    /**
     * save normal command result for trigger
     *
     * @param profile
     * @param success
     */
    public void saveNormalCmds(TriggerProfile profile, boolean success) {
        CommandEntity entity = new CommandEntity();
        entity.setId(CommandEntity.generateCommanid(profile.getTriggerId(), profile.getOpType()));
        entity.setTaskId(profile.getTriggerId());
        entity.setDeliveryTime(profile.getDeliveryTime());
        entity.setCommandResult(success ? MANAGER_SUCCESS_CODE : MANAGER_FAIL_CODE);
        entity.setAcked(false);
        storeCommand(entity);
    }

    /**
     * save special command result for trigger (retry\makeup\check)
     *
     * @param id
     * @param taskId
     * @param success
     */
    public void saveSpecialCmds(Integer id, Integer taskId, boolean success) {
        CommandEntity entity = new CommandEntity();
        entity.setId(String.valueOf(id));
        entity.setTaskId(String.valueOf(taskId));
        entity.setAcked(false);
        entity.setCommandResult(success ? MANAGER_SUCCESS_CODE : MANAGER_FAIL_CODE);
        storeCommand(entity);
    }
}
