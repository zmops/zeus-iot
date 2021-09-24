package com.zmops.zeus.iot.server.transfer.conf;

/**
 * profile used in trigger. Trigger profile is a special job profile
 */
public class TriggerProfile extends JobProfile {

    @Override
    public boolean allRequiredKeyExist() {
        return hasKey(JobConstants.JOB_NAME) && super.allRequiredKeyExist();
    }

    public static TriggerProfile parseJsonStr(String jsonStr) {
        TriggerProfile conf = new TriggerProfile();
        conf.loadJsonStrResource(jsonStr);
        return conf;
    }

    public String getTriggerId() {
        return get(JobConstants.JOB_ID);
    }

    public static TriggerProfile parseJobProfile(JobProfile jobProfile) {
        TriggerProfile conf = new TriggerProfile();
        conf.loadJsonStrResource(jobProfile.toJsonStr());
        return conf;
    }

    public Integer getOpType() {
        return getInt(JobConstants.JOB_OP);
    }

    public String getDeliveryTime() {
        return get(JobConstants.JOB_DELIVERY_TIME);
    }
}
