package com.zmops.iot.web.device.service.work;


import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.util.ToolUtil;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yefei
 * <p>
 * 更新设备 在线状态
 */
@Slf4j
@Component
public class DeviceOnlineWorker implements IWorker<Map<String, String>, Boolean> {

    @Override
    public Boolean action(Map<String, String> param, Map<String, WorkerWrapper<?, ?>> allWrappers) {
        log.debug("更新设备 在线状态…………");

        String deviceId = param.get("hostname");
        if (ToolUtil.isEmpty(deviceId)) {
            return false;
        }
        Device device = new QDevice().deviceId.eq(deviceId).findSingleAttribute();
        if (null == device) {
            return false;
        }
        int    status   = 0;
        String recovery = param.get("recovery");
        if (ToolUtil.isNotEmpty(recovery) && "RESOLVED".equals(recovery)) {
            status = 1;
        }
        device.setOnline(status);
        DB.update(device);

        return true;
    }


    @Override
    public Boolean defaultValue() {
        return true;
    }

}
