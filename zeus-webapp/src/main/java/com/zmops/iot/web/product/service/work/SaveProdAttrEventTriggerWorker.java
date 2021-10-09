package com.zmops.iot.web.product.service.work;


import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.web.product.dto.ProductAttrEvent;
import com.zmops.zeus.driver.service.ZbxTrigger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

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

    @Override
    public Boolean action(ProductAttrEvent productAttr, Map<String, WorkerWrapper<?, ?>> map) {
        log.debug("处理产品 新增Attr事件 告警工作…………");

        String prodId = productAttr.getProductId();
        StringBuilder expression = new StringBuilder();
        expression.append("count(/");
        expression.append(prodId);
        expression.append("/");
        expression.append(productAttr.getKey());
        expression.append(",#1");
        expression.append(") >0 ");
        zbxTrigger.triggerCreate(productAttr.getProductId(), expression.toString(), productAttr.getEventLevel());

        return true;
    }


    @Override
    public Boolean defaultValue() {
        return true;
    }

}
