package com.zmops.iot.web.device.service.event;

import com.zmops.iot.domain.device.Device;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.iot.web.event.applicationEvent.DeviceSaveEvent;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author yefei
 * <p>
 * 更新设备中zbxID
 */
@Slf4j
@Component
@Order(2)
public class UpdateDeviceZbxIdEventHandler implements ApplicationListener<DeviceSaveEvent> {

    @Override
    public void onApplicationEvent(DeviceSaveEvent event) {
        if (null == event.getEventData()) {
            return;
        }
        DeviceDto deviceDto = event.getEventData();

        log.debug("step 8:resolve zbxID async----DEVICEID:{}， HOSTID:{}…………", deviceDto.getDeviceId(),
                deviceDto.getZbxId());

        DB.update(Device.class).where().eq("deviceId", deviceDto.getDeviceId()).asUpdate()
                .set("zbxId", deviceDto.getZbxId()).update();

        log.debug("step 8:resolve zbxID async----DEVICEID:{}， HOSTID:{} 完成", deviceDto.getDeviceId(),
                deviceDto.getZbxId());
    }

}
