package com.zmops.iot.web.product.service.work;


import com.alibaba.fastjson.JSON;
import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.web.product.dto.ProductAttrEvent;
import com.zmops.zeus.driver.service.ZbxTrigger;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SaveProdAttrEventTriggerWorker implements IWorker<ProductAttrEvent, Boolean> {

    @Autowired
    private ZbxTrigger zbxTrigger;
    private static final String EVENT_TAG_NAME   = "__event__";

    @Override
    public Boolean action(ProductAttrEvent productAttr, Map<String, WorkerWrapper<?, ?>> map) {
        log.debug("SaveProdAttrEventTriggerWorker…………");

        String prodId = productAttr.getProductId();
        StringBuilder expression = new StringBuilder();
        expression.append("count(/");
        expression.append(prodId);
        expression.append("/");
        expression.append(productAttr.getKey());
        expression.append(",#1");
        expression.append(") >0 ");
        String res = zbxTrigger.triggerCreate(productAttr.getProductId(), expression.toString(), productAttr.getEventLevel());

        String[] triggerids = JSON.parseObject(res, TriggerIds.class).getTriggerids();
        Map<String, String> tags = new ConcurrentHashMap<>(1);
        tags.put(EVENT_TAG_NAME, prodId + "");
        zbxTrigger.triggerTagCreate(triggerids[0], tags);

        return true;
    }


    @Override
    public Boolean defaultValue() {
        return true;
    }

    @Data
    static class TriggerIds {
        private String[] triggerids;
    }
}
