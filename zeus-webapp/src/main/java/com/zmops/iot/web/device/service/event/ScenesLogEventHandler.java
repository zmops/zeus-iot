package com.zmops.iot.web.device.service.event;

import com.zmops.iot.domain.device.ScenesTriggerRecord;
import com.zmops.iot.domain.product.ProductEvent;
import com.zmops.iot.domain.product.query.QProductEvent;
import com.zmops.iot.web.event.applicationEvent.DeviceSceneLogEvent;
import com.zmops.iot.web.event.applicationEvent.dto.LogEventData;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@EnableAsync
public class ScenesLogEventHandler {

    @Async
    @EventListener(classes = {DeviceSceneLogEvent.class})
    public void onApplicationEvent(DeviceSceneLogEvent event) {
        log.debug("insert into ScenesLogWorker…………");
        LogEventData eventData = event.getEventData();

        long eventRuleId = eventData.getEventRuleId();
        String triggerType = eventData.getTriggerType();
        Long triggerUser = eventData.getTriggerUser();

        ProductEvent productEvent = new QProductEvent().eventRuleId.eq(eventRuleId).findOne();
        if (productEvent == null) {
            return;
        }

        ScenesTriggerRecord scenesTriggerRecord = new ScenesTriggerRecord();
        scenesTriggerRecord.setRuleId(eventRuleId);
        scenesTriggerRecord.setRuleName(productEvent.getEventRuleName());
        scenesTriggerRecord.setCreateTime(LocalDateTime.now());
        scenesTriggerRecord.setTenantId(productEvent.getTenantId());
        scenesTriggerRecord.setTriggerType(triggerType);
        scenesTriggerRecord.setTriggerUser(triggerUser);

        DB.save(scenesTriggerRecord);
    }
}
