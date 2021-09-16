package com.zmops.iot.web.product.controller;

import cn.hutool.core.util.IdUtil;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.product.ProductEvent;
import com.zmops.iot.domain.product.ProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEvent;
import com.zmops.iot.domain.product.query.QProductEventRelation;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.product.dto.ProductEventDto;
import com.zmops.iot.web.product.dto.ProductEventRule;
import com.zmops.iot.web.product.dto.param.EventParm;
import com.zmops.iot.web.product.service.EventRuleService;
import com.zmops.zeus.driver.service.ZbxTrigger;
import io.ebean.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        Map<String, String> tags = eventRule.getTags();
        if (tags == null || !tags.containsKey(ALARM_TAG_NAME)) {
            tags.put(ALARM_TAG_NAME, eventRule.getEventRuleId() + "");
        }
        if (ToolUtil.isNotEmpty(eventRule.getDeviceServices()) && !tags.containsKey(EXECUTE_TAG_NAME)) {
            tags.put(EXECUTE_TAG_NAME, eventRule.getEventRuleId() + "");
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
                Map<String, String> tags = eventRule.getTags();
                if (tags == null || !tags.containsKey(ALARM_TAG_NAME)) {
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

}
