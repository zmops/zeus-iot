package com.zmops.iot.web.product.service.event;


import com.zmops.iot.domain.product.ProductAttributeEvent;
import com.zmops.iot.domain.product.query.QProductAttributeEvent;
import com.zmops.iot.web.event.applicationEvent.ProductAttrCreateEvent;
import com.zmops.iot.web.product.dto.ProductAttr;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yefei
 * <p>
 * 产品属性修改 同步到设备 步骤
 */
@Slf4j
@Component
public class UpdateAttributeEventEventHandler implements ApplicationListener<ProductAttrCreateEvent> {


    @Override
    @Async
    public void onApplicationEvent(ProductAttrCreateEvent event) {
        log.debug("UpdateAttributeEventWorker…………");
        ProductAttr productAttr = event.getEventData();

        List<ProductAttributeEvent> list = new QProductAttributeEvent().templateId.eq(productAttr.getAttrId()).findList();

        for (ProductAttributeEvent productAttributeEvent : list) {
            productAttributeEvent.setName(productAttr.getAttrName());
            productAttributeEvent.setKey(productAttr.getKey());
            productAttributeEvent.setUnits(productAttr.getUnits());
            productAttributeEvent.setValueType(productAttr.getValueType());
        }
        DB.updateAll(list);

    }

}
