package com.zmops.iot.web.event.applicationEvent;

import com.zmops.iot.web.event.applicationEvent.dto.LogEventData;

/**
 * @author yefei
 **/
public class DeviceSceneLogEvent extends BaseEvent<LogEventData> {
    public DeviceSceneLogEvent(Object source, LogEventData eventData) {
        super(source, eventData);
    }
}
