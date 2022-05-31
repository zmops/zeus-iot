package com.zmops.iot.web.product.service.event;


import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.ProductEventService;
import com.zmops.iot.web.event.applicationEvent.ProductEventTriggerCreateEvent;
import com.zmops.iot.web.product.dto.ProductEventRule;
import com.zmops.zeus.driver.service.ZbxTrigger;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yefei
 * <p>
 * 产品服务创建 同步到设备 步骤
 */
@Slf4j
@Component
@EnableAsync
public class UpdateProductEventTriggerEventHandler implements ApplicationListener<ProductEventTriggerCreateEvent> {

    @Autowired
    ZbxTrigger zbxTrigger;

    @Override
    @Async
    public void onApplicationEvent(ProductEventTriggerCreateEvent event) {
        log.debug("UpdateProductEventTriggerWorker…………");
        ProductEventRule productEventRule = event.getEventData();
        String prodId = productEventRule.getProductId();

        List<String> deviceIds = new QDevice().select(QDevice.Alias.deviceId).productId.eq(Long.parseLong(prodId)).findSingleAttributeList();

        List<ProductEventService> addProductEventServiceList = new ArrayList<>();

        for (String deviceId : deviceIds) {
            productEventRule.getDeviceServices().forEach(productEventService -> {
                ProductEventService newProductEventService = new ProductEventService();
                newProductEventService.setDeviceId(deviceId);
                newProductEventService.setExecuteDeviceId(deviceId);
                newProductEventService.setEventRuleId(productEventRule.getEventRuleId());
                newProductEventService.setServiceId(productEventService.getServiceId());
                addProductEventServiceList.add(newProductEventService);
            });
        }

        DB.saveAll(addProductEventServiceList);

    }

}
