package com.zmops.iot.web.schedule;

public enum MisfireStrategyEnum {

    /**
     * do nothing
     */
    DO_NOTHING("Do nothing"),

    /**
     * fire once now
     */
    FIRE_ONCE_NOW("Fire once now");

    private final String title;

    MisfireStrategyEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static MisfireStrategyEnum match(String name, MisfireStrategyEnum defaultItem) {
        for (MisfireStrategyEnum item : MisfireStrategyEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultItem;
    }

}
