package com.zmops.iot.web.product.service.event;


import com.alibaba.fastjson.JSON;
import com.zmops.iot.web.event.applicationEvent.ProductAttrCreateEvent;
import com.zmops.iot.web.product.dto.ProductAttr;
import com.zmops.zeus.driver.service.ZbxTrigger;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yefei
 * <p>
 * 产品属性事件创建 告警 步骤
 */
@Slf4j
@Component
public class SaveProdAttrEventTriggerEventHandler implements ApplicationListener<ProductAttrCreateEvent> {

    @Autowired
    private ZbxTrigger zbxTrigger;
    private static final String EVENT_TAG_NAME = "__event__";

    @Override
    @Async
    public void onApplicationEvent(ProductAttrCreateEvent event) {
        log.debug("SaveProdAttrEventTriggerWorker…………");
        ProductAttr productAttr = event.getEventData();
        String prodId = productAttr.getProductId();
        StringBuilder expression = new StringBuilder();
        expression.append("count(/");
        expression.append(prodId);
        expression.append("/");
        expression.append(productAttr.getKey());
        expression.append(",#1");
        expression.append(") >0 ");
        String res = zbxTrigger.triggerCreate(productAttr.getAttrName(), expression.toString(), productAttr.getEventLevel(),1);

        String[] triggerids = JSON.parseObject(res, TriggerIds.class).getTriggerids();
        Map<String, String> tags = new ConcurrentHashMap<>(1);
        tags.put(EVENT_TAG_NAME, "{HOST.HOST}##" + productAttr.getKey());
        zbxTrigger.triggerTagCreate(triggerids[0], tags);

    }

    @Data
    static class TriggerIds {
        private String[] triggerids;
    }
}
