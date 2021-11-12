package com.zmops.iot.web.schedule;

/**
 * 调度任务类型
 */
public enum ScheduleTypeEnum {

    NONE("None"),

    /**
     * schedule by cron
     */
    CRON("Cron");

    private final String title;

    ScheduleTypeEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static ScheduleTypeEnum match(String name, ScheduleTypeEnum defaultItem) {
        for (ScheduleTypeEnum item : ScheduleTypeEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultItem;
    }

}
