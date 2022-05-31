package com.zmops.iot.web.device.service.event;

import com.zmops.iot.domain.device.DevicesGroups;
import com.zmops.iot.domain.device.query.QDevicesGroups;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.iot.web.event.applicationEvent.DeviceSaveEvent;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yefei
 * <p>
 * 处理设备与设备组关系处理步骤
 */
@Slf4j
@Component
@Order(1)
public class SaveDeviceGrpEventHandler implements ApplicationListener<DeviceSaveEvent> {

    @Override
    public void onApplicationEvent(DeviceSaveEvent event) {
        log.debug("step 4:SaveDeviceGrpWorker----DEVICEID:{}…………", event.getEventData().getDeviceId());
        if (null == event.getEventData()) {
            return;
        }
        DeviceDto deviceDto = event.getEventData();
        //修改模式 先清空关联关系
        if (null != deviceDto.getDeviceId()) {
            new QDevicesGroups().deviceId.eq(deviceDto.getDeviceId()).delete();
        }

        //保存设备与设备组关系
        List<DevicesGroups> devicesGroupsList = new ArrayList<>();
        for (Long deviceGroupId : deviceDto.getDeviceGroupIds()) {
            devicesGroupsList.add(
                    DevicesGroups.builder().deviceId(deviceDto.getDeviceId()).deviceGroupId(deviceGroupId).build());
        }
        DB.saveAll(devicesGroupsList);
        log.debug("step 4:SaveDeviceGrpWorker----DEVICEID:{} complate…………", deviceDto.getDeviceId());
    }

}
