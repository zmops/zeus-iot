package com.zmops.iot.web.device.service.work;


import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.enums.CommonStatus;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceDto;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yefei
 *
 * 保存设备
 */
@Slf4j
@Component
public class SaveDeviceWorker implements IWorker<DeviceDto, Device> {

    @Override
    public Device action(DeviceDto deviceDto, Map<String, WorkerWrapper<?, ?>> allWrappers) {
        log.debug("处理device工作…………");

        Device device = new Device();
        ToolUtil.copyProperties(deviceDto, device);

        if (null == deviceDto.getDeviceId()) {
            device.setStatus(CommonStatus.ENABLE.getCode());
            DB.save(device);
        } else {
            DB.update(device);
        }

        return device;
    }


    @Override
    public Device defaultValue() {
        return new Device();
    }

}
