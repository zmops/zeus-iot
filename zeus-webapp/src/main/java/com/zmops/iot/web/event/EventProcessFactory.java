package com.zmops.iot.web.event;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yefei
 **/
public class EventProcessFactory {

    private static final Map<String, EventProcess> EVENT_PROCESS_MAP = new HashMap<>();

    public static void register(String tag, EventProcess eventProcess) {
        EVENT_PROCESS_MAP.put(tag, eventProcess);
    }

    public static EventProcess getExecuteService(String key) {
        return EVENT_PROCESS_MAP.get(key);
    }
}
