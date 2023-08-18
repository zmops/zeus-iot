package com.zmops.iot.web.device.service.event;

import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.ServiceExecuteRecord;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.ProductEventService;
import com.zmops.iot.domain.product.ProductService;
import com.zmops.iot.domain.product.ProductServiceParam;
import com.zmops.iot.domain.product.query.QProductEventService;
import com.zmops.iot.domain.product.query.QProductService;
import com.zmops.iot.util.DefinitionsUtil;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.event.applicationEvent.DeviceSceneLogEvent;
import com.zmops.iot.web.event.applicationEvent.dto.LogEventData;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@EnableAsync
public class ServiceLogEventHandler {

    @Async
    @EventListener(classes = {DeviceSceneLogEvent.class})
    public void onApplicationEvent(DeviceSceneLogEvent event) {
        log.debug("insert into service log…………");
        LogEventData eventData = event.getEventData();

        long eventRuleId = eventData.getEventRuleId();
        String executeType = eventData.getTriggerType();
        Long executeUser = eventData.getTriggerUser();

        List<ProductEventService> productEventServiceList = new QProductEventService().eventRuleId.eq(eventRuleId).findList();
        List<Long> serviceIds = productEventServiceList.parallelStream().map(ProductEventService::getServiceId).collect(Collectors.toList());

        List<ProductService> productServiceList = new QProductService().id.in(serviceIds).findList();
        Map<Long, ProductService> productServiceMap = productServiceList.parallelStream().collect(Collectors.toMap(ProductService::getId, o -> o, (a, b) -> a));

        List<String> deviceIds = productEventServiceList.parallelStream().map(ProductEventService::getExecuteDeviceId).collect(Collectors.toList());
        List<Device> deviceList = new QDevice().deviceId.in(deviceIds).tenantId.isNotNull().findList();
        Map<String, Long> deviceIdMap = deviceList.parallelStream().collect(Collectors.toMap(Device::getDeviceId, Device::getTenantId, (a, b) -> a));

        List<ServiceExecuteRecord> serviceExecuteRecordList = new ArrayList<>();
        productEventServiceList.forEach(productEventService -> {
            ServiceExecuteRecord serviceExecuteRecord = new ServiceExecuteRecord();
            serviceExecuteRecord.setDeviceId(productEventService.getExecuteDeviceId());
            List<ProductServiceParam> paramList = DefinitionsUtil.getServiceParam(productEventService.getServiceId());
            if (ToolUtil.isNotEmpty(paramList)) {
                serviceExecuteRecord.setParam(JSONObject.toJSONString(paramList.parallelStream().collect(Collectors.toMap(ProductServiceParam::getKey, ProductServiceParam::getValue, (a, b) -> a))));
            }
            serviceExecuteRecord.setServiceName(Optional.ofNullable(productServiceMap.get(productEventService.getServiceId())).map(ProductService::getName).orElse(""));
            if (deviceIdMap.get(productEventService.getExecuteDeviceId()) != null) {
                serviceExecuteRecord.setTenantId(deviceIdMap.get(productEventService.getExecuteDeviceId()));
            }
            serviceExecuteRecord.setCreateTime(LocalDateTime.now());
            serviceExecuteRecord.setExecuteRuleId(eventRuleId);
            serviceExecuteRecord.setExecuteType(executeType);
            serviceExecuteRecord.setExecuteUser(executeUser);

            serviceExecuteRecordList.add(serviceExecuteRecord);
        });
        DB.saveAll(serviceExecuteRecordList);
    }
}
