package com.zmops.iot.web.product.service.event;


import cn.hutool.core.util.IdUtil;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.ProductAttributeEvent;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.event.applicationEvent.ProductAttrCreateEvent;
import com.zmops.iot.web.product.dto.ProductAttr;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yefei
 * <p>
 * 产品属性事件创建 同步到设备 步骤
 */
@Slf4j
@Component
public class SaveProdAttrEventEventHandler implements ApplicationListener<ProductAttrCreateEvent> {


    @Override
    @Async
    public void onApplicationEvent(ProductAttrCreateEvent event) {
        log.debug("SaveProdAttrEventTriggerWorker…………");
        ProductAttr productAttr = event.getEventData();
        String prodId = productAttr.getProductId();

        int count = new QDevice().deviceId.eq(prodId).findCount();
        if (count > 0) {
            return;
        }

        List<String> deviceIds = new QDevice().select(QDevice.Alias.deviceId).productId.eq(Long.parseLong(prodId)).findSingleAttributeList();

        List<ProductAttributeEvent> productAttributeEventList = new ArrayList<>();

        for (String deviceId : deviceIds) {
            ProductAttributeEvent productAttributeEvent = new ProductAttributeEvent();
            ToolUtil.copyProperties(productAttr, productAttributeEvent);
            productAttributeEvent.setAttrId(IdUtil.getSnowflake().nextId());
            productAttributeEvent.setName(productAttr.getAttrName());
            productAttributeEvent.setProductId(deviceId);
            productAttributeEvent.setTemplateId(productAttr.getAttrId());
            productAttributeEventList.add(productAttributeEvent);
        }
        DB.saveAll(productAttributeEventList);

    }

}
