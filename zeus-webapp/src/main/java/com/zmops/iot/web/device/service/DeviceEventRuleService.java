package com.zmops.iot.web.device.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.product.ProductEvent;
import com.zmops.iot.domain.product.ProductEventExpression;
import com.zmops.iot.domain.product.ProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEventExpression;
import com.zmops.iot.domain.product.query.QProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEventService;
import com.zmops.iot.enums.CommonStatus;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductEventRule;
import com.zmops.iot.web.product.dto.ProductEventRuleDto;
import com.zmops.zeus.driver.service.ZbxTrigger;
import io.ebean.DB;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yefei
 **/
@Service
public class DeviceEventRuleService {

    @Autowired
    private ZbxTrigger zbxTrigger;


    public void updateDeviceEventRule(Long eventRuleId, ProductEventRule eventRule) {

        //step 1: 函数表达式
        DB.sqlUpdate("delete from product_event_expression where event_rule_id = :eventRuleId")
                .setParameter("eventRuleId", eventRule.getEventRuleId())
                .execute();

        //step 2: 删除服务方法调用
        DB.sqlUpdate("delete from product_event_service where event_rule_id = :eventRuleId")
                .setParameter("eventRuleId", eventRule.getEventRuleId())
                .execute();

        // 删除和所有设备的关联关系
        new QProductEventRelation().eventRuleId.eq(eventRule.getEventRuleId()).delete();

        // step 3: 保存产品告警规则
        ProductEvent event = initEventRule(eventRule);
        event.setEventRuleId(eventRuleId);
        DB.update(event);

        //step 4: 保存 表达式，方便回显
        List<ProductEventExpression> expList = new ArrayList<>();

        eventRule.getExpList().forEach(i -> {
            ProductEventExpression exp = initEventExpression(i);
            exp.setEventRuleId(eventRuleId);
            expList.add(exp);
        });

        DB.saveAll(expList);

        //step 5: 保存触发器 调用 本产品方法
        if (null != eventRule.getDeviceServices() && !eventRule.getDeviceServices().isEmpty()) {
            eventRule.getDeviceServices().forEach(i -> {
                DB.sqlUpdate("insert into product_event_service(event_rule_id, device_id, service_id) values (:eventRuleId, :deviceId, :serviceId)")
                        .setParameter("eventRuleId", eventRuleId)
                        .setParameter("deviceId", eventRule.getProductId())
                        .setParameter("executeDeviceId", i.getExecuteDeviceId())
                        .setParameter("serviceId", i.getServiceId())
                        .execute();
            });
        }

        // step 6: 保存关联关系
        List<String> relationIds = eventRule.getExpList().parallelStream().map(ProductEventRule.Expression::getDeviceId).distinct().collect(Collectors.toList());
        if (ToolUtil.isEmpty(relationIds)) {
            throw new ServiceException(BizExceptionEnum.EVENT_HAS_NOT_DEVICE);
        }
        List<ProductEventRelation> productEventRelationList = new ArrayList<>();
        relationIds.forEach(relationId -> {
            ProductEventRelation productEventRelation = new ProductEventRelation();
            productEventRelation.setEventRuleId(eventRuleId);
            productEventRelation.setRelationId(relationId);
            productEventRelationList.add(productEventRelation);
        });
        DB.saveAll(productEventRelationList);
    }


    private ProductEvent initEventRule(ProductEventRule eventRule) {
        ProductEvent event = new ProductEvent();
        event.setEventLevel(eventRule.getEventLevel());
        event.setExpLogic(eventRule.getExpLogic());
        event.setEventNotify(eventRule.getEventNotify());
        event.setRemark(eventRule.getRemark());
        event.setEventRuleName(eventRule.getEventRuleName());
        event.setStatus(CommonStatus.ENABLE.getCode());
        return event;
    }

    private ProductEventExpression initEventExpression(ProductEventRule.Expression exp) {
        ProductEventExpression eventExpression = new ProductEventExpression();
        eventExpression.setEventExpId(exp.getEventExpId());
        eventExpression.setCondition(exp.getCondition());
        eventExpression.setFunction(exp.getFunction());
        eventExpression.setScope(exp.getScope());
        eventExpression.setValue(exp.getValue());
        eventExpression.setDeviceId(exp.getDeviceId());
        eventExpression.setProductAttrKey(exp.getProductAttrKey());
        eventExpression.setUnit(exp.getUnit());
        return eventExpression;
    }

    /**
     * 更新 触发器规则 zbxId
     *
     * @param triggerId 规则ID
     * @param zbxId     triggerId
     */
    public void updateProductEventRuleZbxId(Long triggerId, Integer[] zbxId) {

        List<Triggers> triggers = JSONObject.parseArray(zbxTrigger.triggerGet(Arrays.toString(zbxId)), Triggers.class);

        Map<String, Integer> map = triggers.parallelStream().collect(Collectors.toMap(o -> o.hosts.get(0).host, Triggers::getTriggerid));

        List<ProductEventRelation> productEventRelationList = new QProductEventRelation().eventRuleId.eq(triggerId).findList();

        productEventRelationList.forEach(productEventRelation -> {
            if (null != map.get(productEventRelation.getRelationId())) {
                DB.update(ProductEventRelation.class).where().eq("eventRuleId", triggerId).eq("relationId", productEventRelation.getRelationId())
                        .asUpdate().set("zbxId", map.get(productEventRelation.getRelationId())).update();
            }
        });

    }


    public ProductEventRuleDto detail(ProductEvent productEvent, long eventRuleId, String deviceId) {
        ProductEventRuleDto productEventRuleDto = new ProductEventRuleDto();
        ToolUtil.copyProperties(productEvent, productEventRuleDto);

        productEventRuleDto.setExpList(new QProductEventExpression().eventRuleId.eq(eventRuleId).findList());
        productEventRuleDto.setDeviceServices(new QProductEventService().eventRuleId.eq(eventRuleId).deviceId.eq(deviceId).findList());

        ProductEventRelation productEventRelation = new QProductEventRelation().relationId.eq(deviceId).eventRuleId.eq(eventRuleId).findOne();

        if (null != productEventRelation) {
            JSONArray triggerInfo = JSONObject.parseArray(zbxTrigger.triggerAndTagsGet(productEventRelation.getZbxId()));
            productEventRuleDto.setTags(JSONObject.parseArray(triggerInfo.getJSONObject(0).getString("tags"), ProductEventRuleDto.Tag.class));
            productEventRuleDto.setZbxId(productEventRelation.getZbxId());
        }

        return productEventRuleDto;
    }

    /**
     * 创建 触发器
     *
     * @param triggerName 触发器名称
     * @param expression  表达式
     * @param level       告警等级
     * @return 触发器ID
     */
    public Integer[] createZbxTrigger(String triggerName, String expression, Byte level) {
        String res = zbxTrigger.triggerCreate(triggerName, expression, level);
        return JSON.parseObject(res, TriggerIds.class).getTriggerids();
    }

    @Data
    static class TriggerIds {
        private Integer[] triggerids;
    }

    @Data
    public static class Triggers {
        private Integer     triggerid;
        private String      description;
        private List<Hosts> hosts;
    }

    @Data
    static class Hosts {
        private String hostid;
        private String host;
    }
}
