package com.zmops.iot.web.product.controller;

import cn.hutool.core.util.IdUtil;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.product.ProductEvent;
import com.zmops.iot.domain.product.ProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEvent;
import com.zmops.iot.domain.product.query.QProductEventExpression;
import com.zmops.iot.domain.product.query.QProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEventService;
import com.zmops.iot.enums.CommonStatus;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductEventDto;
import com.zmops.iot.web.product.dto.ProductEventRule;
import com.zmops.iot.web.product.dto.param.EventParm;
import com.zmops.iot.web.product.service.ProductEventRuleService;
import com.zmops.zeus.driver.service.ZbxTrigger;
import io.ebean.DB;
import io.ebean.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author nantian created at 2021/8/26 23:04
 * <p>
 * 产品 物模型功能 【告警规则】
 */

@RestController
@RequestMapping("/product/event/trigger")
public class ProductEventTriggerController {


    @Autowired
    private ProductEventRuleService productEventRuleService;

    @Autowired
    private ZbxTrigger zbxTrigger;

    private static final String ALARM_TAG_NAME   = "__alarm__";
    private static final String EXECUTE_TAG_NAME = "__execute__";
    private static final String EVENT_TAG_NAME   = "__event__";
    private static final String EVENT_TYPE_NAME  = "事件";


    /**
     * 触发器 分页列表
     *
     * @param eventParm
     * @return
     */
    @PostMapping("/getEventByPage")
    public Pager<ProductEventDto> getEventByPage(@Validated @RequestBody EventParm eventParm) {
        return productEventRuleService.getEventByPage(eventParm);
    }

    /**
     * 触发器 详情
     *
     * @param eventRuleId
     * @return
     */
    @GetMapping("/detail")
    public ResponseData detail(@RequestParam("eventRuleId") long eventRuleId, @RequestParam("prodId") String prodId) {
        ProductEvent productEvent = new QProductEvent().eventRuleId.eq(eventRuleId).findOne();
        if (null == productEvent) {
            throw new ServiceException(BizExceptionEnum.EVENT_NOT_EXISTS);
        }
        return ResponseData.success(productEventRuleService.detail(productEvent, eventRuleId, prodId));
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

        productEventRuleService.createProductEventRule(eventRuleId, eventRule);

        //step 1: 先创建 zbx 触发器
        String expression = eventRule.getExpList()
                .stream().map(Object::toString).collect(Collectors.joining(" " + eventRule.getExpLogic() + " "));

        //step 2: zbx 保存触发器
        Integer[] triggerIds = productEventRuleService.createZbxTrigger(eventRuleId + "", expression, eventRule.getEventLevel());

        //step 4: zbx 触发器创建 Tag
        Map<String, String> tags = eventRule.getTags().stream()
                .collect(Collectors.toMap(ProductEventRule.Tag::getTag, ProductEventRule.Tag::getValue, (k1, k2) -> k2));
        if (ToolUtil.isEmpty(tags)) {
            tags = new HashMap<>(3);
        }
        if (!tags.containsKey(ALARM_TAG_NAME)) {
            tags.put(ALARM_TAG_NAME, eventRuleId + "");
        }
        if (ToolUtil.isNotEmpty(eventRule.getDeviceServices()) && !tags.containsKey(EXECUTE_TAG_NAME)) {
            tags.put(EXECUTE_TAG_NAME, eventRuleId + "");
        }
        Optional<ProductEventRule.Expression> any = eventRule.getExpList().parallelStream().filter(o -> EVENT_TYPE_NAME.equals(o.getProductAttrType())).findAny();
        if(any.isPresent()){
            tags.put(EVENT_TAG_NAME, eventRuleId + "");
        }
        for (Integer triggerId : triggerIds) {
            zbxTrigger.triggerTagCreate(triggerId, tags);
        }

        //step 5: 更新 zbxId
        productEventRuleService.updateProductEventRuleZbxId(eventRuleId, triggerIds);

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
    public ResponseData updateProductEventRule(@RequestBody @Validated(value = BaseEntity.Update.class) ProductEventRule eventRule) {

        //step 1: 更新所有服务
        productEventRuleService.updateProductEventRule(eventRule.getEventRuleId(), eventRule);

        //step 2: 更新zbx表达式
        String expression = eventRule.getExpList()
                .stream().map(Object::toString).collect(Collectors.joining(" " + eventRule.getExpLogic() + " "));

        ProductEvent event = new QProductEvent().eventRuleId.eq(eventRule.getEventRuleId()).findOne();
        if (event == null) {
            throw new ServiceException(BizExceptionEnum.EVENT_NOT_EXISTS);
        }

        List<ProductEventRelation> list = new QProductEventRelation().eventRuleId.eq(event.getEventRuleId()).findList();
        if (list.isEmpty()) {
            throw new ServiceException(BizExceptionEnum.EVENT_EXPRESSION_NOT_EXISTS);
        }

        zbxTrigger.triggerUpdate(list.get(0).getZbxId(), expression, eventRule.getEventLevel());


        //step 3: zbx 触发器创建 Tag
        Map<String, String> tags = eventRule.getTags().stream()
                .collect(Collectors.toMap(ProductEventRule.Tag::getTag, ProductEventRule.Tag::getValue, (k1, k2) -> k2));

        if (ToolUtil.isEmpty(tags)) {
            tags = new HashMap<>(2);
        }
        if (!tags.containsKey(ALARM_TAG_NAME)) {
            tags.put(ALARM_TAG_NAME, eventRule.getEventRuleId() + "");
        }
        if (ToolUtil.isNotEmpty(eventRule.getDeviceServices()) && !tags.containsKey(EXECUTE_TAG_NAME)) {
            tags.put(EXECUTE_TAG_NAME, eventRule.getEventRuleId() + "");
        }
        Optional<ProductEventRule.Expression> any = eventRule.getExpList().parallelStream().filter(o -> EVENT_TYPE_NAME.equals(o.getProductAttrType())).findAny();
        if(any.isPresent()){
            tags.put(EVENT_TAG_NAME, eventRule.getEventRuleId() + "");
        }

        zbxTrigger.triggerTagCreate(list.get(0).getZbxId(), tags);

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
        DB.update(ProductEventRelation.class).where().eq("eventRuleId", eventRule.getEventRuleId()).asUpdate().set("status", eventRule.getStatus()).update();

        ProductEventRelation productEventRelation = new QProductEventRelation().eventRuleId.eq(eventRule.getEventRuleId()).inherit.eq("0").findOne();

        if (null != productEventRelation && null != productEventRelation.getZbxId()) {
            zbxTrigger.triggerStatusUpdate(productEventRelation.getZbxId(), eventRule.getStatus().equals(CommonStatus.ENABLE.getCode()) ? "0" : "1");
        }

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
        ProductEventRelation productEventRelation = new QProductEventRelation().eventRuleId.eq(eventRule.getEventRuleId()).inherit.eq("0").findOne();
        if (null == productEventRelation) {
            return ResponseData.success();
        }
        //step 1:删除 与产品 设备的关联
        new QProductEventRelation().eventRuleId.eq(eventRule.getEventRuleId()).delete();

        //step 2:删除 关联的执行服务
        new QProductEventService().eventRuleId.eq(eventRule.getEventRuleId()).delete();

        //step 3:删除 关联的表达式
        new QProductEventExpression().eventRuleId.eq(eventRule.getEventRuleId()).delete();

        //step 4:删除 触发器
        new QProductEvent().eventRuleId.eq(eventRule.getEventRuleId()).delete();

        //step 5:删除 zbx触发器

        zbxTrigger.triggerDelete(productEventRelation.getZbxId());
        return ResponseData.success();
    }
}
