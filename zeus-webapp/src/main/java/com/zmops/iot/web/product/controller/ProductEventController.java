package com.zmops.iot.web.product.controller;

import cn.hutool.core.util.IdUtil;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.product.ProductEvent;
import com.zmops.iot.domain.product.ProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEvent;
import com.zmops.iot.domain.product.query.QProductEventExpression;
import com.zmops.iot.domain.product.query.QProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEventService;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.product.dto.ProductEventDto;
import com.zmops.iot.web.product.dto.ProductEventRule;
import com.zmops.iot.web.product.dto.ProductTag;
import com.zmops.iot.web.product.dto.param.EventParm;
import com.zmops.iot.web.product.service.EventRuleService;
import com.zmops.zeus.driver.service.ZbxTrigger;
import io.ebean.DB;
import io.ebean.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author nantian created at 2021/8/26 23:04
 * <p>
 * 产品 物模型功能【事件】 类似于 属性处理
 */

@RestController
@RequestMapping("/product/event")
public class ProductEventController {


    @Autowired
    private EventRuleService eventRuleService;

    @Autowired
    private ZbxTrigger zbxTrigger;

    private static final String ALARM_TAG_NAME   = "__alarm__";
    private static final String EXECUTE_TAG_NAME = "__execute__";


    /**
     * 触发器 分页列表
     *
     * @param eventParm
     * @return
     */
    @PostMapping("/getEventByPage")
    public Pager<ProductEventDto> getEventByPage(@RequestBody EventParm eventParm) {
        return eventRuleService.getEventByPage(eventParm);
    }

    /**
     * 创建 触发器
     *
     * @param eventRule 触发器规则
     * @return 触发器ID
     */
    @Transactional
    @PostMapping("/create")
    public ResponseData createProductEventRule(@RequestBody @Validated(value = BaseEntity.Create.class)
                                                       ProductEventRule eventRule) {

        Long eventRuleId = IdUtil.getSnowflake().nextId(); // ruleId, trigger name

        eventRuleService.createProductEventRule(eventRuleId, eventRule);

        //step 1: 先创建 zbx 触发器
        String expression = eventRule.getExpList()
                .stream().map(Object::toString).collect(Collectors.joining(" " + eventRule.getExpLogic() + " "));

        //step 2: zbx 保存触发器
        String[] triggerIds = eventRuleService.createZbxTrigger(eventRuleId + "", expression, eventRule.getEventLevel());
        
        //step 4: zbx 触发器创建 Tag
        Map<String, String> tags = eventRule.getTags().stream()
                .collect(Collectors.toMap(ProductEventRule.Tag::getTag, ProductEventRule.Tag::getValue, (k1, k2) -> k2));
        if (ToolUtil.isEmpty(tags)){
            tags = new HashMap<>(2);
        }
        if (!tags.containsKey(ALARM_TAG_NAME)) {
            tags.put(ALARM_TAG_NAME, eventRuleId + "");
        }
        if (ToolUtil.isNotEmpty(eventRule.getDeviceServices()) && !tags.containsKey(EXECUTE_TAG_NAME)) {
            tags.put(EXECUTE_TAG_NAME, eventRuleId + "");
        }
        for (String triggerId : triggerIds) {
            zbxTrigger.triggerTagCreate(triggerId, tags);
        }


        //step 5: 更新 zbxId
        eventRuleService.updateProductEventRuleZbxId(eventRuleId, triggerIds);

        // 返回触发器ID
        return ResponseData.success(eventRuleId);
    }

    /**
     * 修改 触发器
     *
     * @param eventRule 触发器规则
     * @return 触发器ID
     */
    @Transactional
    @PostMapping("/update")
    public ResponseData updateProductEventRule(@RequestBody @Validated(value = BaseEntity.Update.class)
                                                       ProductEventRule eventRule) {

        //step 1: 更新所有服务
        eventRuleService.updateProductEventRule(eventRule.getEventRuleId(), eventRule);

        //step 2: 更新zbx表达式
        String expression = eventRule.getExpList()
                .stream().map(Object::toString).collect(Collectors.joining(" " + eventRule.getExpLogic() + " "));


        ProductEvent event = new QProductEvent().eventRuleId.eq(eventRule.getEventRuleId()).findOne();
        if (null != event) {
            List<ProductEventRelation> list = new QProductEventRelation().eventRuleId.eq(event.getEventRuleId()).findList();
            list.forEach(productEventRelation -> {
                zbxTrigger.triggerUpdate(productEventRelation.getZbxId(), expression, eventRule.getEventLevel());

                //step 3: zbx 触发器创建 Tag
                Map<String, String> tags = eventRule.getTags().stream()
                        .collect(Collectors.toMap(ProductEventRule.Tag::getTag, ProductEventRule.Tag::getValue, (k1, k2) -> k2));
                if (ToolUtil.isEmpty(tags)){
                    tags = new HashMap<>(2);
                }
                if (!tags.containsKey(ALARM_TAG_NAME)) {
                    tags.put(ALARM_TAG_NAME, eventRule.getEventRuleId() + "");
                }
                if (ToolUtil.isNotEmpty(eventRule.getDeviceServices()) && !tags.containsKey(EXECUTE_TAG_NAME)) {
                    tags.put(EXECUTE_TAG_NAME, eventRule.getEventRuleId() + "");
                }
                zbxTrigger.triggerTagCreate(productEventRelation.getZbxId(), tags);
            });
        }

        return ResponseData.success(eventRule.getEventRuleId());
    }

    /**
     * 修改 触发器
     *
     * @param eventRule 触发器规则
     * @return 触发器ID
     */
    @PostMapping("/status")
    public ResponseData updateProductEventStatus(@RequestBody @Validated(value = BaseEntity.Status.class) ProductEventRule eventRule) {
        DB.update(ProductEvent.class).where().eq("eventRuleId",eventRule.getEventRuleId()).asUpdate().set("status",eventRule.getStatus()).update();
        return ResponseData.success();
    }

    /**
     * 删除 触发器
     *
     * @param eventRule 触发器规则
     * @return 触发器ID
     */
    @Transactional
    @PostMapping("/delete")
    public ResponseData deleteProductEventRule(@RequestBody @Validated(value = BaseEntity.Delete.class)
                                                       ProductEventRule eventRule) {
        //step 1:删除 与产品 设备的关联
        new QProductEventRelation().eventRuleId.eq(eventRule.getEventRuleId()).delete();

        //step 2:删除 关联的执行服务
        new QProductEventService().eventRuleId.eq(eventRule.getEventRuleId()).delete();

        //step 3:删除 关联的表达式
        new QProductEventExpression().eventRuleId.eq(eventRule.getEventRuleId()).delete();

        //step 4:删除 触发器
        new QProductEvent().eventRuleId.eq(eventRule.getEventRuleId()).delete();

        return ResponseData.success();
    }
}