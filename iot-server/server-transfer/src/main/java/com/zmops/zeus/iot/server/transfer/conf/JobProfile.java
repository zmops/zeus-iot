package com.zmops.zeus.iot.server.transfer.conf;

import com.google.gson.Gson;


/**
 * job profile which contains details describing properties of one job.
 */
public class JobProfile extends AbstractConfiguration {

    private final Gson gson = new Gson();

    /**
     * parse json string to configuration instance。
     *
     * @param jsonStr
     * @return job configuration
     */
    public static JobProfile parseJsonStr(String jsonStr) {
        JobProfile conf = new JobProfile();
        conf.loadJsonStrResource(jsonStr);
        return conf;
    }

    /**
     * parse properties file
     *
     * @param fileName - file name.
     * @return jobConfiguration.
     */
    public static JobProfile parsePropertiesFile(String fileName) {
        JobProfile conf = new JobProfile();
        conf.loadPropertiesResource(fileName);
        return conf;
    }

    /**
     * pase json file.
     *
     * @param fileName - json file name.
     * @return jobConfiguration.
     */
    public static JobProfile parseJsonFile(String fileName) {
        JobProfile conf = new JobProfile();
        conf.loadJsonResource(fileName);
        return conf;
    }

    /**
     * check whether required keys exists.
     *
     * @return return true if all required keys exists else false.
     */
    @Override
    public boolean allRequiredKeyExist() {
        return hasKey(JobConstants.JOB_ID);
    }

    public String toJsonStr() {
        return gson.toJson(getConfigStorage());
    }

    public String getInstanceId() {
        return get(JobConstants.JOB_INSTANCE_ID);
    }
}
