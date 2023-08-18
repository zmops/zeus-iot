package com.zmops.iot.web.product.service.event;


import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.ProductEventRelation;
import com.zmops.iot.domain.product.ProductEventService;
import com.zmops.iot.enums.CommonStatus;
import com.zmops.iot.enums.InheritStatus;
import com.zmops.iot.web.event.applicationEvent.ProductEventTriggerCreateEvent;
import com.zmops.iot.web.product.dto.ProductEventRule;
import com.zmops.iot.web.product.service.ProductEventRuleService;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 产品服务创建 同步到设备 步骤
 */
@Slf4j
@Component
@EnableAsync
public class SaveProductEventTriggerEventHandler implements ApplicationListener<ProductEventTriggerCreateEvent> {


    @Autowired
    ZbxTrigger zbxTrigger;

    @Override
    @Async
    public void onApplicationEvent(ProductEventTriggerCreateEvent event) {
        log.debug("SaveProductEventTriggerWorker…………");
        ProductEventRule productEventRule = event.getEventData();
        String prodId = productEventRule.getProductId();

        List<String> deviceIds = new QDevice().select(QDevice.Alias.deviceId).productId.eq(Long.parseLong(prodId)).findSingleAttributeList();
        List<ProductEventRuleService.Triggers> triggers = JSONObject.parseArray(zbxTrigger.triggerGetByName(productEventRule.getEventRuleId() + ""), ProductEventRuleService.Triggers.class);

        Map<String, String> treiggerMap = triggers.parallelStream().collect(Collectors.toMap(o -> o.getHosts().get(0).getHost(), ProductEventRuleService.Triggers::getTriggerid, (a, b) -> a));

        List<ProductEventRelation> productEventRelationList = new ArrayList<>();
        List<ProductEventService> addProductEventServiceList = new ArrayList<>();

        for (String deviceId : deviceIds) {
            ProductEventRelation productEventRelation = new ProductEventRelation();
            productEventRelation.setRelationId(deviceId);
            productEventRelation.setEventRuleId(productEventRule.getEventRuleId());
            productEventRelation.setInherit(InheritStatus.YES.getCode());
            productEventRelation.setStatus(CommonStatus.ENABLE.getCode());
            productEventRelation.setRemark(productEventRule.getRemark());
            if (null != treiggerMap.get(deviceId)) {
                productEventRelation.setZbxId(treiggerMap.get(deviceId));
            }
            productEventRelationList.add(productEventRelation);

            productEventRule.getDeviceServices().forEach(productEventService -> {
                ProductEventService newProductEventService = new ProductEventService();
                newProductEventService.setDeviceId(deviceId);
                newProductEventService.setExecuteDeviceId(deviceId);
                newProductEventService.setEventRuleId(productEventRule.getEventRuleId());
                newProductEventService.setServiceId(productEventService.getServiceId());
                addProductEventServiceList.add(newProductEventService);
            });
        }
        DB.saveAll(productEventRelationList);

        DB.saveAll(addProductEventServiceList);

    }

}
