package com.zmops.iot.web.event.pgEvent.service;

import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.ProductStatusFunctionRelation;
import com.zmops.iot.domain.product.query.QProductStatusFunctionRelation;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.event.pgEvent.EventProcess;
import com.zmops.iot.web.event.pgEvent.dto.EventDataDto;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author yefei
 * <p>
 * 设备离线处理
 **/
@Slf4j
@Component
public class OfflineEventProcess implements EventProcess {

    @Override
    public void process(EventDataDto eventData) {
        log.debug("update device offline status…………");

        ProductStatusFunctionRelation relation = new QProductStatusFunctionRelation().zbxId.eq(eventData.getObjectid()).findOne();
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
