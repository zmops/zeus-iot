package com.zmops.zeus.iot.server.transfer.core.db;

import com.zmops.zeus.iot.server.transfer.conf.CommonConstants;
import com.zmops.zeus.iot.server.transfer.conf.JobConstants;
import com.zmops.zeus.iot.server.transfer.conf.TriggerProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * db interface for trigger profile.
 * <p>
 * 保存 文件夹触发器 配置文件
 */
public class TriggerProfileDb {

    private static final Logger LOGGER = LoggerFactory.getLogger(TriggerProfileDb.class);

    private final Db db;

    public TriggerProfileDb(Db db) {
        this.db = db;
    }

    /**
     * get trigger list from db.
     *
     * @return - list of trigger
     */
    public List<TriggerProfile> getTriggers() {
        // potential performance issue, needs to find out the speed.
        List<KeyValueEntity> result = this.db.findAll(CommonConstants.TRIGGER_ID_PREFIX);

        List<TriggerProfile> triggerList = new ArrayList<>();

        for (KeyValueEntity entity : result) {
            triggerList.add(entity.getAsTriggerProfile());
        }

        return triggerList;
    }

    /**
     * store trigger profile.
     *
     * @param trigger - trigger
     */
    public void storeTrigger(TriggerProfile trigger) {
        if (trigger.allRequiredKeyExist()) {
            String keyName = CommonConstants.TRIGGER_ID_PREFIX + trigger.get(JobConstants.JOB_ID);

            KeyValueEntity entity = new KeyValueEntity(keyName, trigger.toJsonStr(), trigger.get(JobConstants.JOB_DIR_FILTER_PATTERN));

            KeyValueEntity oldEntity = db.put(entity);

            if (oldEntity != null) {
                LOGGER.warn("trigger profile {} has been replaced", oldEntity.getKey());
            }
        }
    }

    /**
     * delete trigger by id.
     *
     * @param id
     */
    public void deleteTrigger(String id) {
        db.remove(CommonConstants.TRIGGER_ID_PREFIX + id);
    }
}
