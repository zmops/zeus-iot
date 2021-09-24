package com.zmops.zeus.iot.server.transfer.api;

import com.zmops.zeus.iot.server.transfer.conf.JobProfile;
import com.zmops.zeus.iot.server.transfer.conf.TriggerProfile;

import java.io.IOException;

/**
 * Trigger interface, which generates job in condition.
 */
public interface Trigger {

    /**
     * init trigger by trigger profile
     *
     * @param profile
     * @throws IOException
     */
    void init(TriggerProfile profile) throws IOException;

    /**
     * run trigger.
     */
    void run();

    /**
     * destroy trigger.
     */
    void destroy();

    /**
     * fetch job profile from trigger
     *
     * @return - job profile
     */
    JobProfile fetchJobProfile();

    /**
     * get trigger profile
     *
     * @return
     */
    TriggerProfile getTriggerProfile();
}
