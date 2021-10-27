package com.zmops.iot.web.event.service;

import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.ProductStatusFunctionRelation;
import com.zmops.iot.domain.product.query.QProductStatusFunctionRelation;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.event.EventProcess;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author yefei
 **/
@Slf4j
@Component
public class OfflineEventProcess implements EventProcess{

    @Override
    public void process(String triggerId,String triggerName) {
        log.debug("更新设备 离线状态…………");

        ProductStatusFunctionRelation relation = new QProductStatusFunctionRelation().zbxIdRecovery.eq(triggerId).findOne();
        if (null == relation) {
            return;
        }
        String deviceId = relation.getRelationId();
        if (ToolUtil.isEmpty(deviceId)) {
            return;
        }
        Device device = new QDevice().deviceId.eq(deviceId).findOne();
        if (null == device) {
            return;
        }

        DB.update(Device.class).where().eq("deviceId", device.getDeviceId()).asUpdate()
                .set("online", 0).set("latestOnline", LocalDateTime.now()).update();
    }

    @Override
    public boolean checkTag(String tag) {
        return "__offline__".equals(tag);
    }
}
