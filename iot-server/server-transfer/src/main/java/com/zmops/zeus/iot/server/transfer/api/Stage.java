package com.zmops.zeus.iot.server.transfer.api;

import com.zmops.zeus.iot.server.transfer.conf.JobProfile;

/**
 * Stage definition.
 */
public interface Stage {

    /**
     * Init job.
     *
     * @param jobConf - job config
     */
    void init(JobProfile jobConf);

    /**
     * Destroy job.
     */
    void destroy();
}
