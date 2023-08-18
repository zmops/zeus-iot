package com.zmops.iot.web.event.pgEvent.service;

import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.EventTriggerRecord;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.ProductAttributeEvent;
import com.zmops.iot.domain.product.query.QProductAttributeEvent;
import com.zmops.iot.web.analyse.dto.LatestDto;
import com.zmops.iot.web.analyse.service.LatestService;
import com.zmops.iot.web.event.pgEvent.EventProcess;
import com.zmops.iot.web.event.pgEvent.dto.EventDataDto;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * @author yefei
 *
 * 事件处理
 **/
@Slf4j
@Component
public class IncidentEventProcess implements EventProcess {

    @Autowired
    LatestService latestService;

    @Override
    public void process(EventDataDto eventData) {
        String triggerId = eventData.getObjectid();
        String tagValue = eventData.getTagValue();

        log.debug("-------- event----------triggerId:{},tagValue:{}", triggerId, tagValue);

        String[] split = tagValue.split("##");
        if (split.length != 2) {
            return;
        }
        String deviceId = split[0];
        String key = split[1];

        Device device = new QDevice().deviceId.eq(deviceId).findOne();
        ProductAttributeEvent productAttributeEvent = new QProductAttributeEvent().productId.eq(deviceId).key.eq(key).findOne();

        //取事件属性最新值
        List<LatestDto> latestDtos = latestService.queryEventLatest(device.getZbxId(), Collections.singletonList(productAttributeEvent.getZbxId()),
                Integer.parseInt(productAttributeEvent.getValueType()));

        EventTriggerRecord eventTriggerRecord = new EventTriggerRecord();
        eventTriggerRecord.setCreateTime(LocalDateTime.now());
        eventTriggerRecord.setDeviceId(deviceId);
        eventTriggerRecord.setEventName(productAttributeEvent.getName());
        eventTriggerRecord.setEventValue(latestDtos.get(0).getOriginalValue());
        eventTriggerRecord.setTenantId(device.getTenantId());
        eventTriggerRecord.setKey(productAttributeEvent.getKey());

        DB.insert(eventTriggerRecord);
    }

    @Override
    public boolean checkTag(String tag) {
        return "__event__".equals(tag);
    }
}
