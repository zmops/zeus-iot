package com.zmops.iot.web.device.service.work;


import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.DevicesGroups;
import com.zmops.iot.domain.device.query.QDevicesGroups;
import com.zmops.iot.web.device.dto.DeviceDto;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yefei
 * <p>
 * 处理设备与设备组关系处理步骤
 */
@Slf4j
@Component
public class SaveDeviceGrpWorker implements IWorker<DeviceDto, Boolean> {


    @Override
    public Boolean action(DeviceDto deviceDto, Map<String, WorkerWrapper<?, ?>> map) {
        log.debug("处理设备与设备组关系工作…………");

        //修改模式 先清空关联关系
        if (null != deviceDto.getDeviceId()) {
            new QDevicesGroups().deviceId.eq(deviceDto.getDeviceId()).delete();
        }

        //保存设备与设备组关系
        Device device = (Device) map.get("saveDvice").getWorkResult().getResult();
        List<DevicesGroups> devicesGroupsList = new ArrayList<>();
        for (Long deviceGroupId : deviceDto.getDeviceGroupIds()) {
            devicesGroupsList.add(DevicesGroups.builder().deviceId(device.getDeviceId()).deviceGroupId(deviceGroupId).build());
        }
        DB.saveAll(devicesGroupsList);

        return true;
    }


    @Override
    public Boolean defaultValue() {
        return true;
    }

}
