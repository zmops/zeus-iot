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
import com.zmops.iot.enums.InheritStatus;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceEventRule;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductEventRuleDto;
import com.zmops.zeus.driver.service.ZbxTrigger;
import io.ebean.DB;
import io.ebean.annotation.Transactional;
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


    /**
     * 保存触发器
     *
     * @param eventRuleId 触发器ID
     * @param eventRule   告警规则
     */
    @Transactional(rollbackFor = Exception.class)
    public void createDeviceEventRule(Long eventRuleId, DeviceEventRule eventRule) {
        // step 1: 保存产品告警规则
        ProductEvent event = initEventRule(eventRule);
        event.setEventRuleId(eventRuleId);
        DB.save(event);

        //step 2: 保存 表达式，方便回显
        List<ProductEventExpression> expList = new ArrayList<>();

        eventRule.getExpList().forEach(i -> {
            ProductEventExpression exp = initEventExpression(i);
            exp.setEventRuleId(eventRuleId);
            expList.add(exp);
        });

        DB.saveAll(expList);

        //step 3: 保存触发器 调用 本产品方法
        if (null != eventRule.getDeviceServices() && !eventRule.getDeviceServices().isEmpty()) {
            List<String> deviceIds = eventRule.getExpList().parallelStream().map(DeviceEventRule.Expression::getDeviceId).distinct().collect(Collectors.toList());
            deviceIds.forEach(deviceId -> {
                eventRule.getDeviceServices().forEach(i -> {
                    DB.sqlUpdate("insert into product_event_service(event_rule_id, device_id,execute_device_id, service_id) " +
                                    "values (:eventRuleId, :deviceId,:executeDeviceId, :serviceId)")
                            .setParameter("eventRuleId", eventRuleId)
                            .setParameter("executeDeviceId", i.getExecuteDeviceId())
                            .setParameter("serviceId", i.getServiceId())
                            .execute();
                });
            });
        }

        //step 4: 保存关联关系
        List<String> relationIds = eventRule.getExpList().parallelStream().map(DeviceEventRule.Expression::getDeviceId).distinct().collect(Collectors.toList());
        if (ToolUtil.isEmpty(relationIds)) {
            throw new ServiceException(BizExceptionEnum.EVENT_HAS_NOT_DEVICE);
        }
        List<ProductEventRelation> productEventRelationList = new ArrayList<>();
        relationIds.forEach(relationId -> {
            ProductEventRelation productEventRelation = new ProductEventRelation();
            productEventRelation.setEventRuleId(eventRuleId);
            productEventRelation.setRelationId(relationId);
            productEventRelation.setInherit(InheritStatus.NO.getCode());
            productEventRelation.setStatus(CommonStatus.ENABLE.getCode());
            productEventRelation.setRemark(eventRule.getRemark());
            productEventRelationList.add(productEventRelation);
        });
        DB.saveAll(productEventRelationList);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateDeviceEventRule(Long eventRuleId, DeviceEventRule eventRule) {

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
        event.update();

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
            List<String> deviceIds = eventRule.getExpList().parallelStream().map(DeviceEventRule.Expression::getDeviceId).distinct().collect(Collectors.toList());
            deviceIds.forEach(deviceId -> {
                eventRule.getDeviceServices().forEach(i -> {
                    DB.sqlUpdate("insert into product_event_service(event_rule_id, device_id, service_id) values (:eventRuleId, :deviceId, :serviceId)")
                            .setParameter("eventRuleId", eventRuleId)
                            .setParameter("deviceId", deviceId)
                            .setParameter("executeDeviceId", i.getExecuteDeviceId())
                            .setParameter("serviceId", i.getServiceId())
                            .execute();
                });
            });
        }

        // step 6: 保存关联关系
        List<String> relationIds = eventRule.getExpList().parallelStream().map(DeviceEventRule.Expression::getDeviceId).distinct().collect(Collectors.toList());
        if (ToolUtil.isEmpty(relationIds)) {
            throw new ServiceException(BizExceptionEnum.EVENT_HAS_NOT_DEVICE);
        }
        List<ProductEventRelation> productEventRelationList = new ArrayList<>();
        relationIds.forEach(relationId -> {
            ProductEventRelation productEventRelation = new ProductEventRelation();
            productEventRelation.setEventRuleId(eventRuleId);
            productEventRelation.setRelationId(relationId);
            productEventRelation.setStatus(CommonStatus.ENABLE.getCode());
            productEventRelation.setRemark(eventRule.getRemark());
            productEventRelationList.add(productEventRelation);
        });
        DB.saveAll(productEventRelationList);
    }


    private ProductEvent initEventRule(DeviceEventRule eventRule) {
        ProductEvent event = new ProductEvent();
        event.setEventLevel(eventRule.getEventLevel().toString());
        event.setExpLogic(eventRule.getExpLogic());
        event.setEventNotify(eventRule.getEventNotify().toString());
        event.setClassify(eventRule.getClassify());
        event.setEventRuleName(eventRule.getEventRuleName());
        return event;
    }

    private ProductEventExpression initEventExpression(DeviceEventRule.Expression exp) {
        ProductEventExpression eventExpression = new ProductEventExpression();
        eventExpression.setEventExpId(exp.getEventExpId());
        eventExpression.setCondition(exp.getCondition());
        eventExpression.setFunction(exp.getFunction());
        eventExpression.setScope(exp.getScope());
        eventExpression.setValue(exp.getValue());
        eventExpression.setDeviceId(exp.getDeviceId());
        eventExpression.setProductAttrKey(exp.getProductAttrKey());
        eventExpression.setProductAttrId(exp.getProductAttrId());
        eventExpression.setProductAttrType(exp.getProductAttrType());
        eventExpression.setPeriod(exp.getPeriod());
        eventExpression.setUnit(exp.getUnit());
        return eventExpression;
    }

    /**
     * 更新 触发器规则 zbxId
     *
     * @param triggerId 规则ID
     * @param zbxId     triggerId
     */
    public void updateProductEventRuleZbxId(Long triggerId, String[] zbxId) {

        List<Triggers> triggers = JSONObject.parseArray(zbxTrigger.triggerGet(Arrays.toString(zbxId)), Triggers.class);

        Map<String, String> map = triggers.parallelStream().collect(Collectors.toMap(o -> o.hosts.get(0).host, Triggers::getTriggerid));

        List<ProductEventRelation> productEventRelationList = new QProductEventRelation().eventRuleId.eq(triggerId).findList();

        productEventRelationList.forEach(productEventRelation -> {
            if (null != map.get(productEventRelation.getRelationId())) {
                DB.update(ProductEventRelation.class).where().eq("eventRuleId", triggerId).eq("relationId", productEventRelation.getRelationId())
                        .asUpdate().set("zbxId", map.get(productEventRelation.getRelationId())).update();
            }
        });
    }

    /**
     * 获取产品事件规则详情
     *
     * @param productEvent
     * @param eventRuleId
     * @param deviceId
     * @return
     */
    public ProductEventRuleDto detail(ProductEvent productEvent, long eventRuleId, String deviceId) {
        ProductEventRuleDto productEventRuleDto = new ProductEventRuleDto();
        ToolUtil.copyProperties(productEvent, productEventRuleDto);

        List<ProductEventExpression> expList = new QProductEventExpression().eventRuleId.eq(eventRuleId).findList();
        expList.forEach(productEventExpression -> {
            if (ToolUtil.isEmpty(productEventExpression.getDeviceId())) {
                productEventExpression.setDeviceId(deviceId);
            }
        });
        productEventRuleDto.setExpList(expList);
        productEventRuleDto.setDeviceServices(new QProductEventService().eventRuleId.eq(eventRuleId).deviceId.eq(deviceId).findList());

        ProductEventRelation productEventRelation = new QProductEventRelation().relationId.eq(deviceId).eventRuleId.eq(eventRuleId).findOne();
        productEventRuleDto.setStatus(productEventRelation.getStatus());
        productEventRuleDto.setRemark(productEventRelation.getRemark());
        productEventRuleDto.setInherit(productEventRelation.getInherit());
        if (InheritStatus.YES.getCode().equals(productEventRelation.getInherit())) {
            ProductEventRelation one = new QProductEventRelation().eventRuleId.eq(eventRuleId).inherit.eq(InheritStatus.NO.getCode()).findOne();
            productEventRuleDto.setInheritProductId(one.getRelationId());
        }

        JSONArray triggerInfo = JSONObject.parseArray(zbxTrigger.triggerAndTagsGet(productEventRelation.getZbxId()));
        List<ProductEventRuleDto.Tag> tagList = JSONObject.parseArray(triggerInfo.getJSONObject(0).getString("tags"), ProductEventRuleDto.Tag.class);

        productEventRuleDto.setZbxId(productEventRelation.getZbxId());
        productEventRuleDto.setTags(tagList.stream()
                .filter(s -> !s.getTag().equals("__execute__") && !s.getTag().equals("__alarm__") && !s.getTag().equals("__event__"))
                .collect(Collectors.toList()));

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
    public String[] createZbxTrigger(String triggerName, String expression, Byte level) {
        String res = zbxTrigger.triggerCreate(triggerName, expression, level);
        return JSON.parseObject(res, TriggerIds.class).getTriggerids();
    }

    /**
     * 更新 触发器
     *
     * @param triggerId
     * @param expression
     * @param level
     * @return
     */
    public String[] updateZbxTrigger(String triggerId, String expression, Byte level) {
        String res = zbxTrigger.triggerUpdate(triggerId, expression, level);
        return JSON.parseObject(res, TriggerIds.class).getTriggerids();
    }

    @Data
    static class TriggerIds {
        private String[] triggerids;
    }

    @Data
    public static class Triggers {
        private String      triggerid;
        private String      description;
        private List<Hosts> hosts;
    }

    @Data
    static class Hosts {
        private String hostid;
        private String host;
    }
}
