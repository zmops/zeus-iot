package com.zmops.iot.web.device.service.event;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.device.query.QDevicesGroups;
import com.zmops.iot.domain.device.query.QTag;
import com.zmops.iot.domain.product.query.*;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.event.applicationEvent.DeviceDeleteEvent;
import com.zmops.zeus.driver.service.ZbxHost;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@EnableAsync
public class DeleteDeviceEventHandler {

    @Autowired
    private ZbxHost zbxHost;

    @Async
    @EventListener(classes = {DeviceDeleteEvent.class})
    public void onApplicationEvent(DeviceDeleteEvent event) {
        String deviceId = event.getEventData().getDeviceId();
        String zbxId = event.getEventData().getZbxId();

        new QTag().sid.eq(deviceId).delete();

        new QProductAttribute().productId.eq(deviceId).delete();

        new QDevicesGroups().deviceId.eq(deviceId).delete();

        if (ToolUtil.isNotEmpty(zbxId)) {
            JSONArray jsonArray = JSONObject.parseArray(zbxHost.hostDetail(zbxId));
            if (jsonArray.size() > 0) {
                zbxHost.hostDelete(Collections.singletonList(zbxId));
            }
        }

        new QProductStatusFunctionRelation().relationId.eq(deviceId).delete();
        new QProductServiceRelation().relationId.eq(deviceId).delete();
        new QProductEventRelation().relationId.eq(deviceId).delete();
        new QProductEventService().deviceId.eq(deviceId).delete();
        new QProductServiceParam().deviceId.eq(deviceId).delete();
//        new QDeviceServiceMethod().deviceId.eq(deviceId).delete();

        new QDevice().deviceId.eq(deviceId).delete();

    }
}
