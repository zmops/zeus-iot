package com.zmops.iot.web.device.service.work;


import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.device.ServiceExecuteRecord;
import com.zmops.iot.domain.product.ProductEventService;
import com.zmops.iot.domain.product.ProductService;
import com.zmops.iot.domain.product.query.QProductEventService;
import com.zmops.iot.domain.product.query.QProductService;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 更新设备 在线状态
 */
@Slf4j
@Component
public class DeviceServiceLogWorker implements IWorker<Map<String, Object>, Boolean> {

    @Override
    public Boolean action(Map<String, Object> param, Map<String, WorkerWrapper<?, ?>> allWrappers) {
        log.debug("插入 服务 日志…………");

        long eventRuleId = (long) param.get("eventRuleId");
        String relationId = (String) param.get("relationId");

        List<ProductEventService> productEventServiceList = new QProductEventService().eventRuleId.eq(eventRuleId).deviceId.eq(relationId).findList();
        List<Long> serviceIds = productEventServiceList.parallelStream().map(ProductEventService::getServiceId).collect(Collectors.toList());

        List<ProductService> productServiceList = new QProductService().id.in(serviceIds).findList();
        Map<Long, ProductService> productServiceMap = productServiceList.parallelStream().collect(Collectors.toMap(ProductService::getId, o -> o));

        List<ServiceExecuteRecord> serviceExecuteRecordList = new ArrayList<>();
        productEventServiceList.forEach(productEventService -> {
            ServiceExecuteRecord serviceExecuteRecord = new ServiceExecuteRecord();
            serviceExecuteRecord.setDeviceId(productEventService.getDeviceId());
            //TODO 执行的参数
            serviceExecuteRecord.setServiceName(Optional.ofNullable(productServiceMap.get(productEventService.getServiceId())).map(ProductService::getName).orElse(""));
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
