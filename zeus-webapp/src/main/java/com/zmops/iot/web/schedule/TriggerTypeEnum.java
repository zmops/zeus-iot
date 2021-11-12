package com.zmops.iot.web.schedule;

/**
 * 触发类型
 */
public enum TriggerTypeEnum {

    MANUAL("Manual trigger"),
    CRON("Cron trigger"),
    RETRY("Fail retry trigger"),
    PARENT("Parent job trigger"),
    MISFIRE("Misfire compensation trigger");

    private TriggerTypeEnum(String title) {
        this.title = title;
    }

    private final String title;

    public String getTitle() {
        return title;
    }

}
