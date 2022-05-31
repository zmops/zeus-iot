package com.zmops.iot.web.event.applicationEvent;

import com.zmops.iot.web.device.dto.DeviceDto;

/**
 * @author yefei
 **/
public class DeviceSaveEvent extends BaseEvent<DeviceDto> {
    public DeviceSaveEvent(Object source, DeviceDto eventData) {
        super(source, eventData);
    }
}
