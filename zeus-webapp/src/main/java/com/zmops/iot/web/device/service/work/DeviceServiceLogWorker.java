package com.zmops.iot.web.device.service.work;


import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.device.ServiceExecuteRecord;
import com.zmops.iot.domain.product.ProductEventService;
import com.zmops.iot.domain.product.ProductService;
import com.zmops.iot.domain.product.ProductServiceParam;
import com.zmops.iot.domain.product.query.QProductEventService;
import com.zmops.iot.domain.product.query.QProductService;
import com.zmops.iot.util.DefinitionsUtil;
import com.zmops.iot.util.ToolUtil;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 记录服务执行日志
 */
@Slf4j
@Component
public class DeviceServiceLogWorker implements IWorker<Map<String, Object>, Boolean> {

    @Override
    public Boolean action(Map<String, Object> param, Map<String, WorkerWrapper> allWrappers) {
        log.debug("insert into service log…………");

        long eventRuleId = (long) param.get("eventRuleId");
        String executeType = (String) param.get("triggerType");
        Long executeUser = Optional.ofNullable(param.get("triggerUser")).map(Object::toString).map(Long::parseLong).orElse(null);

        List<ProductEventService> productEventServiceList = new QProductEventService().eventRuleId.eq(eventRuleId).findList();
        List<Long> serviceIds = productEventServiceList.parallelStream().map(ProductEventService::getServiceId).collect(Collectors.toList());

        List<ProductService> productServiceList = new QProductService().id.in(serviceIds).findList();
        Map<Long, ProductService> productServiceMap = productServiceList.parallelStream().collect(Collectors.toMap(ProductService::getId, o -> o));

        List<ServiceExecuteRecord> serviceExecuteRecordList = new ArrayList<>();
        productEventServiceList.forEach(productEventService -> {
            ServiceExecuteRecord serviceExecuteRecord = new ServiceExecuteRecord();
            serviceExecuteRecord.setDeviceId(productEventService.getExecuteDeviceId());
            List<ProductServiceParam> paramList = DefinitionsUtil.getServiceParam(productEventService.getServiceId());
            if (ToolUtil.isNotEmpty(paramList)) {
                serviceExecuteRecord.setParam(JSONObject.toJSONString(paramList.parallelStream().collect(Collectors.toMap(ProductServiceParam::getKey, ProductServiceParam::getValue))));
            }
            serviceExecuteRecord.setServiceName(Optional.ofNullable(productServiceMap.get(productEventService.getServiceId())).map(ProductService::getName).orElse(""));
            serviceExecuteRecord.setCreateTime(LocalDateTime.now());
            serviceExecuteRecord.setExecuteRuleId(eventRuleId);
            serviceExecuteRecord.setExecuteType(executeType);
            serviceExecuteRecord.setExecuteUser(executeUser);

            serviceExecuteRecordList.add(serviceExecuteRecord);
        });
        DB.saveAll(serviceExecuteRecordList);

        return true;
    }


    @Override
    public Boolean defaultValue() {
        return true;
    }

}
