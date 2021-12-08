package com.zmops.iot.web.device.service.work;

import com.zmops.iot.domain.device.Device;
import com.zmops.iot.enums.CommonStatus;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.zeus.server.async.callback.IWorker;
import com.zmops.zeus.server.async.wrapper.WorkerWrapper;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yefei
 * <p>
 * 保存设备
 */
@Slf4j
@Component
public class SaveDeviceWorker implements IWorker<DeviceDto, Device> {

    @Override
    public Device action(DeviceDto deviceDto, Map<String, WorkerWrapper> allWrappers) {
        log.debug("step 1:SaveDeviceWorker----DEVICEID:{}…………",deviceDto.getDeviceId());

        Device device = new Device();
        ToolUtil.copyProperties(deviceDto, device);

        if (ToolUtil.isNotEmpty(deviceDto.getEdit()) && "true".equals(deviceDto.getEdit())) {
            DB.update(device);
        } else {
            device.setStatus(CommonStatus.ENABLE.getCode());
            DB.insert(device);
        }
        log.debug("step 1:SaveDeviceWorker----DEVICEID:{} complete",deviceDto.getDeviceId());
        return device;
    }


    @Override
    public Device defaultValue() {
        return new Device();
    }

}
