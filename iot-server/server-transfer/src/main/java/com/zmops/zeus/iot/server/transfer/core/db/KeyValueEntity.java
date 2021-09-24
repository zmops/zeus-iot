package com.zmops.zeus.iot.server.transfer.core.db;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import com.zmops.zeus.iot.server.transfer.conf.JobProfile;
import com.zmops.zeus.iot.server.transfer.conf.TriggerProfile;

/**
 * key value entity. key is string and value is a json
 */
@Entity(version = 1)
public class KeyValueEntity {

    @PrimaryKey
    private String key;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private StateSearchKey stateSearchKey;

    /**
     * stores the file name that the jsonValue refers
     */
    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private String fileName;

    private String jsonValue;

    public KeyValueEntity() {
    }

    public KeyValueEntity(String key, String jsonValue, String fileName) {
        this.key = key;
        this.jsonValue = jsonValue;
        this.stateSearchKey = StateSearchKey.ACCEPTED;
        this.fileName = fileName;
    }

    public String getKey() {
        return key;
    }

    public StateSearchKey getStateSearchKey() {
        return stateSearchKey;
    }

    public KeyValueEntity setStateSearchKey(StateSearchKey stateSearchKey) {
        this.stateSearchKey = stateSearchKey;
        return this;
    }

    public String getJsonValue() {
        return jsonValue;
    }

    public KeyValueEntity setJsonValue(String jsonValue) {
        this.jsonValue = jsonValue;
        return this;
    }

    /**
     * convert keyValue to job profile
     *
     * @return JobConfiguration
     */
    public JobProfile getAsJobProfile() {
        // convert jsonValue to jobConfiguration
        return JobProfile.parseJsonStr(getJsonValue());
    }

    /**
     * convert keyValue to trigger profile
     *
     * @return
     */
    public TriggerProfile getAsTriggerProfile() {
        return TriggerProfile.parseJsonStr(getJsonValue());
    }

    /**
     * check whether the entity is finished
     *
     * @return
     */
    public boolean checkFinished() {
        return stateSearchKey.equals(StateSearchKey.SUCCESS) || stateSearchKey.equals(StateSearchKey.FAILED);
    }
}
