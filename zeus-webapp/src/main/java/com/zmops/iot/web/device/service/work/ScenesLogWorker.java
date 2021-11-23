package com.zmops.iot.web.device.service.work;


import com.zmops.zeus.server.async.callback.IWorker;
import com.zmops.zeus.server.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.device.ScenesTriggerRecord;
import com.zmops.iot.domain.product.ProductEvent;
import com.zmops.iot.domain.product.query.QProductEvent;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * @author yefei
 * <p>
 * 记录 场景 日志
 */
@Slf4j
@Component
public class ScenesLogWorker implements IWorker<Map<String, Object>, Boolean> {

    @Override
    public Boolean action(Map<String, Object> param, Map<String, WorkerWrapper> allWrappers) {
        log.debug("insert into ScenesLogWorker…………");

        long eventRuleId = (long) param.get("eventRuleId");
        String triggerType = (String) param.get("triggerType");
        Long triggerUser = Optional.ofNullable(param.get("triggerUser")).map(Object::toString).map(Long::parseLong).orElse(null);

        ProductEvent productEvent = new QProductEvent().eventRuleId.eq(eventRuleId).findOne();
        if (productEvent == null) {
            return true;
        }
        ScenesTriggerRecord scenesTriggerRecord = new ScenesTriggerRecord();
        scenesTriggerRecord.setRuleId(eventRuleId);
        scenesTriggerRecord.setRuleName(productEvent.getEventRuleName());
        scenesTriggerRecord.setCreateTime(LocalDateTime.now());
        scenesTriggerRecord.setTriggerType(triggerType);
        scenesTriggerRecord.setTriggerUser(triggerUser);


        DB.save(scenesTriggerRecord);

        return true;
    }


    @Override
    public Boolean defaultValue() {
        return true;
    }

}
