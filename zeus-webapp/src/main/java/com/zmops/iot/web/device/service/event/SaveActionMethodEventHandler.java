package com.zmops.iot.web.device.service.event;

import com.zmops.iot.web.event.applicationEvent.DeviceSaveEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author yefei
 * <p>
 * 处理设备服务下发 方法
 */
@Slf4j
@Component
@Order(1)
public class SaveActionMethodEventHandler implements ApplicationListener<DeviceSaveEvent> {

    @Override
    public void onApplicationEvent(DeviceSaveEvent event) {
        log.debug("step 2:SaveActionMethodWorker----DEVICEID:{}…………", event.getEventData().getDeviceId());
//        if (null == event.getEventData()) {
//            return;
//        }
//        DeviceDto deviceDto = event.getEventData();
//        if (ToolUtil.isEmpty(deviceDto.getMethod())) {
//            return;
//        }
//
//        //保存设备服务下发 执行方法
//        DeviceServiceMethod deviceServiceMethod = new DeviceServiceMethod();
//        deviceServiceMethod.setDeviceId(deviceDto.getDeviceId());
//        deviceServiceMethod.setMethod(deviceDto.getMethod());
//
//        DB.save(deviceServiceMethod);
//        log.debug("step 2:SaveActionMethodWorker----DEVICEID:{} complete…………", deviceDto.getDeviceId());
        return;
    }

}
