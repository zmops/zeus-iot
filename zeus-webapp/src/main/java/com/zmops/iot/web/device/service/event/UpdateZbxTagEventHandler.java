package com.zmops.iot.web.device.service.event;

import com.zmops.iot.domain.device.Tag;
import com.zmops.iot.domain.device.query.QTag;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.iot.web.event.applicationEvent.DeviceSaveEvent;
import com.zmops.zeus.driver.service.ZbxHost;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yefei
 * <p>
 * 同步设备标签到zbx
 */
@Slf4j
@Component
@Order(2)
public class UpdateZbxTagEventHandler implements ApplicationListener<DeviceSaveEvent> {

    @Autowired
    ZbxHost zbxHost;

    @Override
    public void onApplicationEvent(DeviceSaveEvent event) {
        log.debug("step 9: UpdateZbxTagWorker ……");
        if (null == event.getEventData()) {
            return;
        }
        DeviceDto deviceDto = event.getEventData();

        //查询出本地tag
        List<Tag> list = new QTag().sid.eq(deviceDto.getDeviceId()).findList();
        Map<String, String> tagMap = new HashMap<>(list.size());
        for (Tag tag : list) {
            tagMap.put(tag.getTag(), tag.getValue());
        }

        //保存
        zbxHost.hostTagUpdate(deviceDto.getZbxId(), tagMap);
        log.debug("step 9:resolve zbxID async----DEVICEID:{}， HOSTID:{} 完成", deviceDto.getDeviceId(),
                deviceDto.getZbxId());
    }

}
