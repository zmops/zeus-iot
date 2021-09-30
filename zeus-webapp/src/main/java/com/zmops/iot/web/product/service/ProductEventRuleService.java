package com.zmops.iot.web.product.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.product.ProductEvent;
import com.zmops.iot.domain.product.ProductEventExpression;
import com.zmops.iot.domain.product.ProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEvent;
import com.zmops.iot.domain.product.query.QProductEventExpression;
import com.zmops.iot.domain.product.query.QProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEventService;
import com.zmops.iot.enums.CommonStatus;
import com.zmops.iot.enums.InheritStatus;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.product.dto.ProductEventDto;
import com.zmops.iot.web.product.dto.ProductEventRule;
import com.zmops.iot.web.product.dto.ProductEventRuleDto;
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
public class ProductEventRuleService {

    @Autowired
    private ZbxTrigger zbxTrigger;

    /**
     * 保存触发器
     *
     * @param eventRule   告警规则
     */
    public void createProductEventRule(ProductEventRule eventRule) {
        // step 1: 保存产品告警规则
        ProductEvent event = initEventRule(eventRule);
        event.setEventRuleId(eventRule.getEventRuleId());
        DB.save(event);

        //step 2: 保存 表达式，方便回显
        List<ProductEventExpression> expList = new ArrayList<>();

        eventRule.getExpList().forEach(i -> {
            ProductEventExpression exp = initEventExpression(i);
            exp.setEventRuleId(eventRule.getEventRuleId());
            expList.add(exp);
        });

        DB.saveAll(expList);

        //step 3: 保存触发器 调用 本产品方法
        if (null != eventRule.getDeviceServices() && !eventRule.getDeviceServices().isEmpty()) {
            eventRule.getDeviceServices().forEach(i -> {
                DB.sqlUpdate("insert into product_event_service(event_rule_id, device_id,execute_device_id, service_id) values (:eventRuleId, :deviceId,:executeDeviceId, :serviceId)")
                        .setParameter("eventRuleId", eventRule.getEventRuleId())
                        .setParameter("executeDeviceId", i.getExecuteDeviceId())
                        .setParameter("serviceId", i.getServiceId())
                        .execute();
            });
        }

        //step 4: 保存关联关系
        ProductEventRelation productEventRelation = new ProductEventRelation();
        productEventRelation.setEventRuleId(eventRule.getEventRuleId());
        productEventRelation.setRelationId(eventRule.getProductId());
        productEventRelation.setStatus(CommonStatus.ENABLE.getCode());
        productEventRelation.setInherit(InheritStatus.NO.getCode());
        productEventRelation.setRemark(eventRule.getRemark());
        DB.save(productEventRelation);
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
                        .setParameter("deviceId", eventRule.getProductId())
                        .setParameter("executeDeviceId", i.getExecuteDeviceId())
                        .setParameter("serviceId", i.getServiceId())
                        .execute();
            });
        }

        //step 6:保存备注
        if (null != eventRule.getRemark()) {
            DB.update(QProductEventRelation.class).where().eq("eventRuleId", eventRule.getEventRuleId()).eq("relationId", eventRule.getProductId())
                    .asUpdate().set("remark", eventRule.getRemark()).update();
        }

    }


    private ProductEvent initEventRule(ProductEventRule eventRule) {
        ProductEvent event = new ProductEvent();
        event.setEventLevel(eventRule.getEventLevel().toString());
        event.setExpLogic(eventRule.getExpLogic());
        event.setEventNotify(eventRule.getEventNotify().toString());
        event.setClassify(eventRule.getClassify());
        event.setEventRuleName(eventRule.getEventRuleName());

        return event;
    }

    private ProductEventExpression initEventExpression(ProductEventRule.Expression exp) {
        ProductEventExpression eventExpression = new ProductEventExpression();
        eventExpression.setEventExpId(exp.getEventExpId());
        eventExpression.setCondition(exp.getCondition());
        eventExpression.setFunction(exp.getFunction());
        eventExpression.setScope(exp.getScope());
        eventExpression.setValue(exp.getValue());
//        eventExpression.setDeviceId(exp.getProductId());
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
     * @param eventRuleId 规则ID
     * @param zbxId       triggerId
     */
    public void updateProductEventRuleZbxId(Long eventRuleId, String[] zbxId) {
        String s = zbxTrigger.triggerGet(Arrays.toString(zbxId));

        List<Triggers> triggers = JSONObject.parseArray(s, Triggers.class);

        Map<String, String> map = triggers.parallelStream().collect(Collectors.toMap(o -> o.hosts.get(0).host, Triggers::getTriggerid));

        List<ProductEventRelation> productEventRelationList = new QProductEventRelation().eventRuleId.eq(eventRuleId).findList();

        productEventRelationList.forEach(productEventRelation -> {
            if (null != map.get(productEventRelation.getRelationId())) {
                DB.update(ProductEventRelation.class).where().eq("eventRuleId", eventRuleId).eq("relationId", productEventRelation.getRelationId())
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
        if (ToolUtil.isNotEmpty(eventParm.getClassify())) {
            query.classify.eq(eventParm.getClassify());
        }
        List<ProductEventRelation> productEventRelationList = new QProductEventRelation().select(QProductEventRelation.alias().eventRuleId)
                .relationId.eq(eventParm.getProdId()).findList();
        if (ToolUtil.isEmpty(productEventRelationList)) {
            return new Pager<>();
        }
        List<Long> eventRuleIdList = productEventRelationList.parallelStream().map(ProductEventRelation::getEventRuleId).collect(Collectors.toList());
        if (ToolUtil.isNotEmpty(eventRuleIdList)) {
            query.eventRuleId.in(eventRuleIdList);
        }
        Map<Long, ProductEventRelation> productEventRelationMap = productEventRelationList.parallelStream().collect(Collectors.toMap(ProductEventRelation::getEventRuleId, o -> o));


        List<ProductEventDto> list = query.setFirstRow((eventParm.getPage() - 1) * eventParm.getMaxRow())
                .setMaxRows(eventParm.getMaxRow()).orderBy(" create_time desc").asDto(ProductEventDto.class).findList();

        list.forEach(productEventDto -> {
            if (null != productEventRelationMap.get(productEventDto.getEventRuleId())) {
                productEventDto.setStatus(productEventRelationMap.get(productEventDto.getEventRuleId()).getStatus());
                productEventDto.setRemark(productEventRelationMap.get(productEventDto.getEventRuleId()).getRemark());
                productEventDto.setIn(productEventRelationMap.get(productEventDto.getEventRuleId()).getStatus());
            }
        });

        return new Pager<>(list, query.findCount());
    }

    /**
     * 获取 告警规则 详情
     *
     * @param productEvent
     * @param eventRuleId
     * @param prodId
     * @return
     */
    public ProductEventRuleDto detail(ProductEvent productEvent, long eventRuleId, String prodId) {
        ProductEventRuleDto productEventRuleDto = new ProductEventRuleDto();
        ToolUtil.copyProperties(productEvent, productEventRuleDto);

        List<ProductEventExpression> expList = new QProductEventExpression().eventRuleId.eq(eventRuleId).findList();
        expList.forEach(productEventExpression -> {
            if (ToolUtil.isEmpty(productEventExpression.getDeviceId())) {
                productEventExpression.setDeviceId(prodId);
            }
        });
        productEventRuleDto.setDeviceServices(new QProductEventService().eventRuleId.eq(eventRuleId).deviceId.isNull().findList());

        ProductEventRelation productEventRelation = new QProductEventRelation().relationId.eq(prodId).eventRuleId.eq(eventRuleId).findOne();
        productEventRuleDto.setStatus(productEventRelation.getStatus());
        productEventRuleDto.setRemark(productEventRelation.getRemark());
        JSONArray triggerInfo = JSONObject.parseArray(zbxTrigger.triggerAndTagsGet(productEventRelation.getZbxId()));

        List<ProductEventRuleDto.Tag> tagList = JSONObject.parseArray(triggerInfo.getJSONObject(0).getString("tags"), ProductEventRuleDto.Tag.class);

        productEventRuleDto.setZbxId(productEventRelation.getZbxId());
        productEventRuleDto.setTags(tagList.stream()
                .filter(s -> !s.getTag().equals("__execute__") && !s.getTag().equals("__alarm__") && !s.getTag().equals("__event__"))
                .collect(Collectors.toList()));

        return productEventRuleDto;
    }


    @Data
    static class TriggerIds {
        private String[] triggerids;
    }

    @Data
    public static class Triggers {
        private String     triggerid;
        private String      description;
        private List<Hosts> hosts;
    }

    @Data
    public static class Hosts {
        private String hostid;
        private String host;
    }
}
