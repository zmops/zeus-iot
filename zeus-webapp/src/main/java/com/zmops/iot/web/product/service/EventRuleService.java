package com.zmops.iot.web.product.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.product.ProductEvent;
import com.zmops.iot.domain.product.ProductEventExpression;
import com.zmops.iot.domain.product.ProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEvent;
import com.zmops.iot.domain.product.query.QProductEventExpression;
import com.zmops.iot.domain.product.query.QProductEventRelation;
import com.zmops.iot.enums.CommonStatus;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.product.dto.ProductEventDto;
import com.zmops.iot.web.product.dto.ProductEventRule;
import com.zmops.iot.web.product.dto.param.EventParm;
import com.zmops.zeus.driver.service.ZbxTrigger;
import io.ebean.DB;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author nantian created at 2021/9/15 13:59
 * <p>
 * 告警规则 创建，产品 和 设备
 */

@Slf4j
@Service
public class EventRuleService {

    @Autowired
    private ZbxTrigger zbxTrigger;

    /**
     * 保存触发器
     *
     * @param eventRuleId 触发器ID
     * @param eventRule   告警规则
     */
    public void createProductEventRule(Long eventRuleId, ProductEventRule eventRule) {
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
            eventRule.getDeviceServices().forEach(i -> {
                DB.sqlUpdate("insert into product_event_service(event_rule_id, device_id,execute_device_id, service_id) values (:eventRuleId, :deviceId,:executeDeviceId, :serviceId)")
                        .setParameter("eventRuleId", eventRuleId)
                        .setParameter("deviceId", eventRuleId)
                        .setParameter("executeDeviceId", i.getExecuteDeviceId())
                        .setParameter("serviceId", i.getServiceId())
                        .execute();
            });
        }

        //step 4: 保存关联关系
        List<String>               relationIds = eventRule.getExpList().parallelStream().map(ProductEventRule.Expression::getProductId).distinct().collect(Collectors.toList());
        List<ProductEventRelation> lists       = new ArrayList<>();
        relationIds.forEach(relationId -> {
            ProductEventRelation productEventRelation = new ProductEventRelation();
            productEventRelation.setEventRuleId(eventRuleId);
            productEventRelation.setRelationId(relationId);
        });


    }

    /**
     * 更新触发器
     *
     * @param eventRuleId 触发器ID
     * @param eventRule   告警规则
     */
    public void updateProductEventRule(Long eventRuleId, ProductEventRule eventRule) {

        //step 1: 函数表达式
        DB.sqlUpdate("delete from product_event_expression where event_rule_id = :eventRuleId")
                .setParameter("eventRuleId", eventRule.getEventRuleId())
                .execute();

        //step 2: 删除服务方法调用
        DB.sqlUpdate("delete from product_event_service where event_rule_id = :eventRuleId")
                .setParameter("eventRuleId", eventRule.getEventRuleId())
                .execute();

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
                        .setParameter("deviceId", i.getDeviceId())
                        .setParameter("serviceId", i.getServiceId())
                        .execute();
            });
        }
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
        eventExpression.setDeviceId(exp.getProductId());
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
    public void updateProductEventRuleZbxId(Long triggerId, String[] zbxId) {

        List<Triggers> triggers = JSONObject.parseArray(zbxTrigger.triggerGet(Arrays.toString(zbxId)), Triggers.class);

        Map<String, String> map = triggers.parallelStream().collect(Collectors.toMap(o -> o.hosts.host, Triggers::getTriggerid));

        List<ProductEventRelation> productEventRelationList = new QProductEventRelation().eventRuleId.eq(triggerId).findList();

        productEventRelationList.forEach(productEventRelation -> {
            if (null != map.get(productEventRelation.getRelationId())) {
                DB.update(ProductEventRelation.class).where().eq("ruleId", triggerId).eq("relationId", productEventRelation.getRelationId())
                        .asUpdate().set("zbxId", map.get(productEventRelation.getRelationId())).update();
            }
        });

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
     * 触发器 分页列表
     *
     * @param eventParm
     * @return
     */
    public Pager<ProductEventDto> getEventByPage(EventParm eventParm) {
        QProductEvent query = new QProductEvent();

        if (ToolUtil.isNotEmpty(eventParm.getEventRuleName())) {
            query.eventRuleName.contains(eventParm.getEventRuleName());
        }

        if (ToolUtil.isNotEmpty(eventParm.getProdId())) {
            List<Long> eventRuleIdList = new QProductEventRelation().select(QProductEventRelation.alias().eventRuleId)
                    .relationId.eq(eventParm.getProdId()).findSingleAttributeList();

            if (ToolUtil.isNotEmpty(eventRuleIdList)) {
                query.eventRuleId.in(eventRuleIdList);
            }
        }

        List<ProductEventDto> list = query.setFirstRow((eventParm.getPage() - 1) * eventParm.getMaxRow())
                .setMaxRows(eventParm.getMaxRow()).orderBy(" create_time desc").asDto(ProductEventDto.class).findList();

        return new Pager<>(list, query.findCount());
    }


    @Data
    static class TriggerIds {
        private String[] triggerids;
    }

    @Data
    public static class Triggers {
        private String triggerid;
        private String description;
        private Hosts  hosts;
    }

    @Data
    static class Hosts {
        private String hostid;
        private String host;
    }
}
