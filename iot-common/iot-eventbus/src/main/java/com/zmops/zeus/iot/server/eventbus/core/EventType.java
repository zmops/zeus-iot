package com.zmops.zeus.iot.server.eventbus.core;

public enum EventType {
    SYNC("Sync"),
    ASYNC("Async");

    private final String value;

    private EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EventType fromString(String value) {
        for (EventType type : EventType.values()) {
            if (type.getValue().equalsIgnoreCase(value.trim())) {
                return type;
            }
        }

        throw new IllegalArgumentException("Mismatched type with value=" + value);
    }

    @Override
    public String toString() {
        return value;
    }
}