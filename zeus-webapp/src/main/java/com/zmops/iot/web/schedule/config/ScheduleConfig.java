package com.zmops.iot.schedule.config;

import lombok.Data;

/**
 * @author nantian created at 2021/11/12 12:07
 */

@Data
public class ScheduleConfig {

    private int triggerPoolFastMax = 200;

    private int triggerPoolSlowMax = 100;

    private int logretentiondays = 30;


    public int getTriggerPoolFastMax() {
        if (triggerPoolFastMax < 200) {
            return 200;
        }
        return triggerPoolFastMax;
    }

    public int getTriggerPoolSlowMax() {
        if (triggerPoolSlowMax < 100) {
            return 100;
        }
        return triggerPoolSlowMax;
    }


    public int getLogretentiondays() {
        if (logretentiondays < 7) {
            return -1;  // Limit greater than or equal to 7, otherwise close
        }
        return logretentiondays;
    }
}
