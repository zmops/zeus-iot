package com.zmops.iot.web.event.applicationEvent;

import com.zmops.iot.web.event.applicationEvent.dto.DeviceDeleteEventData;

/**
 * @author yefei
 **/
public class DeviceDeleteEvent extends BaseEvent<DeviceDeleteEventData> {
    public DeviceDeleteEvent(Object source, DeviceDeleteEventData eventData) {
        super(source, eventData);
    }
}
