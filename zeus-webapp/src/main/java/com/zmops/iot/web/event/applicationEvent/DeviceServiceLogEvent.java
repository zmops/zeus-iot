package com.zmops.iot.web.event.applicationEvent;

import com.zmops.iot.web.event.applicationEvent.dto.LogEventData;

/**
 * @author yefei
 **/
public class DeviceServiceLogEvent extends BaseEvent<LogEventData> {
    public DeviceServiceLogEvent(Object source, LogEventData eventData) {
        super(source, eventData);
    }
}
