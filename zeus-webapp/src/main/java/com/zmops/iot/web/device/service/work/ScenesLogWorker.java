package com.zmops.iot.web.device.service.work;


import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.device.ScenesTriggerRecord;
import com.zmops.iot.domain.product.ProductEvent;
import com.zmops.iot.domain.product.query.QProductEvent;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author yefei
 * <p>
 * 场景 日志
 */
@Slf4j
@Component
public class ScenesLogWorker implements IWorker<Map<String, Object>, Boolean> {

    @Override
    public Boolean action(Map<String, Object> param, Map<String, WorkerWrapper<?, ?>> allWrappers) {
        log.debug("插入 场景 日志…………");

        long eventRuleId = (long) param.get("eventRuleId");
        ProductEvent productEvent = new QProductEvent().eventRuleId.eq(eventRuleId).findOne();
        if (productEvent == null) {
            return true;
        }
        ScenesTriggerRecord scenesTriggerRecord = new ScenesTriggerRecord();
        scenesTriggerRecord.setRuleId(eventRuleId);
        scenesTriggerRecord.setRuleName(productEvent.getEventRuleName());
        scenesTriggerRecord.setCreateTime(LocalDateTime.now());

        DB.save(scenesTriggerRecord);

        return true;
    }


    @Override
    public Boolean defaultValue() {
        return true;
    }

}
