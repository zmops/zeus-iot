package com.zmops.iot.web.event.pgEvent.service;

import com.alibaba.fastjson.JSON;
import com.dtflys.forest.Forest;
import com.zmops.iot.domain.product.ProductEventRelation;
import com.zmops.iot.domain.product.ProductEventService;
import com.zmops.iot.domain.product.ProductServiceParam;
import com.zmops.iot.domain.product.query.QProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEventService;
import com.zmops.iot.util.DefinitionsUtil;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.event.applicationEvent.DeviceServiceLogEvent;
import com.zmops.iot.web.event.applicationEvent.dto.LogEventData;
import com.zmops.iot.web.event.pgEvent.EventProcess;
import com.zmops.iot.web.event.pgEvent.dto.EventDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 告警执行服务
 **/
@Slf4j
@Component
public class ServiceEventProcess implements EventProcess {

    @Autowired
    ApplicationEventPublisher publisher;

    @Override
    public void process(EventDataDto eventData) {
        log.debug("--------service event----------{}", eventData.getObjectid());
        Map<String, Object> alarmInfo = new ConcurrentHashMap<>(3);

        //查询 告警规则
        List<ProductEventRelation> productEventRelationList = new QProductEventRelation().zbxId.eq(eventData.getObjectid()).findList();
        if (ToolUtil.isEmpty(productEventRelationList)) {
            return;
        }

        //记录服务日志
        publisher.publishEvent(new DeviceServiceLogEvent(this, LogEventData.builder().eventRuleId(productEventRelationList.get(0).getEventRuleId())
                .relationId(productEventRelationList.get(0).getRelationId()).triggerType("自动").build()));

        //查询 告警规则 关联的 服务
        List<ProductEventService> productEventServiceList = new QProductEventService()
                .eventRuleId.eq(productEventRelationList.get(0).getEventRuleId())
                .or()
                .deviceId.isNull()
                .deviceId.eq(productEventRelationList.get(0).getRelationId())
                .endOr()
                .findList();

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, List<ProductEventService>> collect = productEventServiceList.parallelStream()
                .collect(Collectors.groupingBy(ProductEventService::getExecuteDeviceId));

        collect.forEach((key, value) -> {
            Map<String, Object> map = new ConcurrentHashMap<>();
            map.put("device", key);

            List<Map<String, Object>> serviceList = new ArrayList<>();
            value.forEach(val -> {
                Map<String, Object> serviceMap = new ConcurrentHashMap<>();
                serviceMap.put("name", DefinitionsUtil.getServiceName(val.getServiceId()));

                List<ProductServiceParam> paramList = DefinitionsUtil.getServiceParam(val.getServiceId());
                if (ToolUtil.isNotEmpty(paramList)) {
                    serviceMap.put("param", paramList.parallelStream().filter(o -> key.equals(o.getDeviceId()))
                            .collect(Collectors.toMap(ProductServiceParam::getKey, ProductServiceParam::getValue, (a, b) -> a)));
                }
                serviceList.add(serviceMap);
            });
            map.put("service", serviceList);
            list.add(map);
        });

        Forest.post("/device/action/exec").host("127.0.0.1").port(12800).contentTypeJson().addBody(JSON.toJSON(list)).execute();
    }

    @Override
    public boolean checkTag(String tag) {
        return "__execute__".equals(tag);
    }

}
