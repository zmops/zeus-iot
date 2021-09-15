package com.zmops.iot.web.product.controller;

import cn.hutool.core.util.IdUtil;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.product.ProductEvent;
import com.zmops.iot.domain.product.ProductEventExpression;
import com.zmops.iot.enums.CommonStatus;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.product.dto.ProductEventDto;
import com.zmops.iot.web.product.dto.ProductEventRule;
import com.zmops.iot.web.product.dto.param.EventParm;
import com.zmops.iot.web.product.service.ProductEventService;
import io.ebean.DB;
import io.ebean.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
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
    private ProductEventService productEventService;

    /**
     * 触发器 分页列表
     *
     * @param eventParm
     * @return
     */
    @PostMapping("/getEventByPage")
    public Pager<ProductEventDto> getEventByPage(@RequestBody EventParm eventParm) {
        return productEventService.getEventByPage(eventParm);
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

        //step 1: 先创建 zbx 触发器
        String expression = eventRule.getExpList()
                .stream().map(Object::toString).collect(Collectors.joining(" " + eventRule.getExpLogic() + " "));

        String triggerId = productEventService.createZbxTrigger(eventRuleId + "", expression, eventRule.getEventLevel());

        //step 2：保存 业务触发器对象
        ProductEvent event = new ProductEvent();
        event.setEventRuleId(eventRuleId);
        event.setEventLevel(eventRule.getEventLevel());
        event.setExpLogic(eventRule.getExpLogic());
        event.setEventNotify(eventRule.getEventNotify());
        event.setRemark(eventRule.getRemark());
        event.setEventRuleName(eventRule.getEventRuleName());
        event.setZbxId(Integer.valueOf(triggerId));
        event.setStatus(CommonStatus.ENABLE.getCode());

        productEventService.createProductEventRule(event);

        //step 3: 保存 表达式，方便回显

        List<ProductEventExpression> expList = new ArrayList<>();

        eventRule.getExpList().forEach(i -> {

            ProductEventExpression exp = new ProductEventExpression();
            exp.setEventRuleId(eventRuleId);
            exp.setCondition(i.getCondition());
            exp.setFunction(i.getFunction());
            exp.setScope(i.getScope());
            exp.setValue(i.getValue());
            exp.setProductId(i.getProductId());
            exp.setProductAttrKey(i.getProductAttrKey());

            expList.add(exp);
        });

        productEventService.createProductEventExpression(expList);

        // step 4: 保存 tag
        if (eventRule.getTags() != null) {
            productEventService.createProductEventTags(triggerId, eventRule.getTags());
        }

        // step 5: 保存 调用 Service
        if (null != eventRule.getDeviceServices() && !eventRule.getDeviceServices().isEmpty()) {
            eventRule.getDeviceServices().forEach(i -> {
                DB.sqlUpdate("insert into product_event_service(event_rule_id, device_id, service_id) values (:eventRuleId, :deviceId, :serviceId)")
                        .setParameter("eventRuleId", eventRuleId)
                        .setParameter("deviceId", i.getDeviceId())
                        .setParameter("serviceId", i.getServiceId())
                        .execute();
            });
        }

        return ResponseData.success(eventRuleId); // 返回触发器ID
    }

    /**
     * 修改 触发器
     *
     * @param eventRule 触发器规则
     * @return 触发器ID
     */
    @Transactional
    @PostMapping("/update")
    public ResponseData updateProductEventRule(@RequestBody @Validated(value = BaseEntity.Create.class)
                                                       ProductEventRule eventRule) {
        return ResponseData.success();
    }

}
