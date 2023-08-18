package com.zmops.iot.web.device.service.event;

import com.zmops.iot.domain.device.query.QTag;
import com.zmops.iot.util.ToolUtil;
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
 * 设备标签处理步骤
 */
@Slf4j
@Component
@Order(2)
public class SaveTagEventHandler implements ApplicationListener<DeviceSaveEvent> {

    @Override
    public void onApplicationEvent(DeviceSaveEvent event) {
        log.debug("step 5:SaveTagWorker----DEVICEID:{}…………", event.getEventData().getDeviceId());
        if (null == event.getEventData()) {
            return;
        }
        DeviceDto deviceDto = event.getEventData();
        String deviceId = deviceDto.getDeviceId();

        if (ToolUtil.isNotEmpty(deviceDto.getEdit()) && "true".equals(deviceDto.getEdit())) {
            //修改

            //没有修改关联的产品 不做处理
            if (deviceDto.getProductId().equals(deviceDto.getOldProductId())) {
                return;
            }
            new QTag().sid.eq(deviceId).delete();
        }

        //继承产品中的标签
        DB.sqlUpdate(
                        "insert into tag (sid,tag,value,template_id) SELECT :deviceId,tag,value,id template_id from tag where sid=:sid")
                .setParameter("deviceId", deviceId).setParameter("sid", deviceDto.getProductId() + "").execute();
        log.debug("step 5:SaveTagWorker----DEVICEID:{} complete", deviceDto.getDeviceId());
    }

}
