package com.zmops.iot.web.device.service;

import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.iot.web.event.applicationEvent.DeviceDeleteEvent;
import com.zmops.iot.web.event.applicationEvent.DeviceSaveEvent;
import com.zmops.iot.web.event.applicationEvent.dto.DeviceDeleteEventData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@EnableAsync
public class DeviceEventPublisher {

    @Autowired
    ApplicationEventPublisher publisher;

    @Async
    public void DeviceSaveEventPublish(DeviceDto deviceDto) {
        publisher.publishEvent(new DeviceSaveEvent(this, deviceDto));
    }

    public void DeviceDeleteEventPublish(String deviceId, String zbxId) {
        publisher.publishEvent(new DeviceDeleteEvent(this, DeviceDeleteEventData.builder().deviceId(deviceId).zbxId(zbxId).build()));
    }
}
