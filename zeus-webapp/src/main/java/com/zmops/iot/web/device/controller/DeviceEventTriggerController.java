package com.zmops.iot.web.device.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.product.ProductEvent;
import com.zmops.iot.domain.product.ProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEvent;
import com.zmops.iot.domain.product.query.QProductEventExpression;
import com.zmops.iot.domain.product.query.QProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEventService;
import com.zmops.iot.enums.CommonStatus;
import com.zmops.iot.enums.InheritStatus;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceEventRule;
import com.zmops.iot.web.device.service.DeviceEventRuleService;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 设备告警规则
 **/
@RestController
@RequestMapping("/device/event/trigger")
public class DeviceEventTriggerController {

    @Autowired
    DeviceEventRuleService deviceEventRuleService;

    @Autowired
    private ZbxTrigger zbxTrigger;

    private static final String ALARM_TAG_NAME = "__alarm__";
    private static final String EXECUTE_TAG_NAME = "__execute__";
    private static final String EVENT_TAG_NAME = "__event__";
    private static final String EVENT_TYPE_NAME = "事件";

    /**
     * 触发器 详情
     *
     * @param eventRuleId
     * @return
     */

    @GetMapping("/detail")
    public ResponseData detail(@RequestParam("eventRuleId") long eventRuleId, @RequestParam("deviceId") String deviceId) {
        ProductEvent productEvent = new QProductEvent().eventRuleId.eq(eventRuleId).findOne();
        if (null == productEvent) {
            throw new ServiceException(BizExceptionEnum.EVENT_NOT_EXISTS);
        }
        return ResponseData.success(deviceEventRuleService.detail(productEvent, eventRuleId, deviceId));
    }

    /**
     * 创建 触发器
     *
     * @param eventRule 触发器规则
     * @return 触发器ID
     */
    @Transactional
    @PostMapping("/create")
    public ResponseData createDeviceEventRule(@RequestBody @Validated(value = BaseEntity.Create.class)
                                                      DeviceEventRule eventRule) {

        Long eventRuleId = IdUtil.getSnowflake().nextId(); // ruleId, trigger name

        deviceEventRuleService.createDeviceEventRule(eventRuleId, eventRule);

        //step 1: 先创建 zbx 触发器
        String expression = eventRule.getExpList()
                .stream().map(Object::toString).collect(Collectors.joining(" " + eventRule.getExpLogic() + " "));

        //step 2: zbx 保存触发器
        String[] triggerIds = deviceEventRuleService.createZbxTrigger(eventRuleId + "", expression, eventRule.getEventLevel());

        //step 4: zbx 触发器创建 Tag
        Map<String, String> tags = new ConcurrentHashMap<>(3);
        if (ToolUtil.isNotEmpty(eventRule.getTags())) {
            tags = eventRule.getTags().stream()
                    .collect(Collectors.toMap(DeviceEventRule.Tag::getTag, DeviceEventRule.Tag::getValue, (k1, k2) -> k2));
        }
        if (!tags.containsKey(ALARM_TAG_NAME)) {
            tags.put(ALARM_TAG_NAME, eventRuleId + "");
        }
        if (ToolUtil.isNotEmpty(eventRule.getDeviceServices()) && !tags.containsKey(EXECUTE_TAG_NAME)) {
            tags.put(EXECUTE_TAG_NAME, eventRuleId + "");
        }
        Optional<DeviceEventRule.Expression> any = eventRule.getExpList().parallelStream().filter(o -> EVENT_TYPE_NAME.equals(o.getProductAttrType())).findAny();
        if (any.isPresent()) {
            tags.put(EVENT_TAG_NAME, eventRuleId + "");
        }
        for (String triggerId : triggerIds) {
            zbxTrigger.triggerTagCreate(triggerId, tags);
        }

        //step 5: 更新 zbxId
        deviceEventRuleService.updateProductEventRuleZbxId(eventRuleId, triggerIds);

        // 返回触发器ID
        return ResponseData.success(eventRuleId);
    }

    /**
     * 修改 触发器状态
     *
     * @param eventRule 触发器规则
     * @return 触发器ID
     */
    @PostMapping("/status")
    public ResponseData updateProductEventStatus(@RequestBody @Validated(value = BaseEntity.Status.class) DeviceEventRule eventRule) {
        DB.update(ProductEventRelation.class).where().eq("eventRuleId", eventRule.getEventRuleId()).eq("relationId", eventRule.getDeviceId()).asUpdate()
                .set("status", eventRule.getStatus()).update();

        ProductEventRelation productEventRelation = new QProductEventRelation().eventRuleId.eq(eventRule.getEventRuleId())
                .relationId.eq(eventRule.getDeviceId()).findOne();

        if (null != productEventRelation && null != productEventRelation.getZbxId()) {
            zbxTrigger.triggerStatusUpdate(productEventRelation.getZbxId(), eventRule.getStatus().equals(CommonStatus.ENABLE.getCode()) ? "0" : "1");
        }

        return ResponseData.success();
    }

    /**
     * 修改 触发器
     *
     * @param eventRule 触发器规则
     * @return 触发器ID
     */
    @Transactional
    @PostMapping("/update")
    public ResponseData updateDeviceEventRule(@RequestBody @Validated(value = BaseEntity.Update.class) DeviceEventRule eventRule) {
        int count = new QProductEventRelation().eventRuleId.eq(eventRule.getEventRuleId())
                .findCount();
        if (count == 0) {
            throw new ServiceException(BizExceptionEnum.EVENT_NOT_EXISTS);
        }
        //来自产品的告警规则 只能修改备注
        if (count > 1) {
            DB.update(ProductEventRelation.class).where().eq("eventRuleId", eventRule.getEventRuleId()).eq("relationId", eventRule.getDeviceId())
                    .asUpdate().set("remark", eventRule.getRemark()).update();

            return ResponseData.success(eventRule.getEventRuleId());
        }

        //step 1: 删除原有的 关联关系
        deviceEventRuleService.updateDeviceEventRule(eventRule.getEventRuleId(), eventRule);

        //step 1: 先创建 zbx 触发器
        String expression = eventRule.getExpList()
                .stream().map(Object::toString).collect(Collectors.joining(" " + eventRule.getExpLogic() + " "));

        //step 2: zbx 保存触发器
        String[] triggerIds = deviceEventRuleService.updateZbxTrigger(eventRule.getZbxId(), expression, eventRule.getEventLevel());

        //step 4: zbx 触发器创建 Tag
        Map<String, String> tags = eventRule.getTags().stream()
                .collect(Collectors.toMap(DeviceEventRule.Tag::getTag, DeviceEventRule.Tag::getValue, (k1, k2) -> k2));
        if (ToolUtil.isEmpty(tags)) {
            tags = new HashMap<>(2);
        }
        if (!tags.containsKey(ALARM_TAG_NAME)) {
            tags.put(ALARM_TAG_NAME, eventRule.getEventRuleId() + "");
        }
        if (ToolUtil.isNotEmpty(eventRule.getDeviceServices()) && !tags.containsKey(EXECUTE_TAG_NAME)) {
            tags.put(EXECUTE_TAG_NAME, eventRule.getEventRuleId() + "");
        }
        Optional<DeviceEventRule.Expression> any = eventRule.getExpList().parallelStream().filter(o -> EVENT_TYPE_NAME.equals(o.getProductAttrType())).findAny();
        if (any.isPresent()) {
            tags.put(EVENT_TAG_NAME, eventRule.getEventRuleId() + "");
        }
        for (String triggerId : triggerIds) {
            zbxTrigger.triggerTagCreate(triggerId, tags);
        }

        //step 5: 更新 zbxId 反写
        deviceEventRuleService.updateProductEventRuleZbxId(eventRule.getEventRuleId(), triggerIds);

        // 返回触发器ID
        return ResponseData.success(eventRule.getEventRuleId());
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
                                                       DeviceEventRule eventRule) {

        ProductEventRelation productEventRelation = new QProductEventRelation().relationId.eq(eventRule.getDeviceId())
                .eventRuleId.eq(eventRule.getEventRuleId()).findOne();

        if (InheritStatus.YES.getCode().equals(productEventRelation.getInherit())) {
            throw new ServiceException(BizExceptionEnum.EVENT_PRODUCT_CANNOT_DELETE);
        }

        //step 01:删除 zbx触发器
        if (ToolUtil.isNotEmpty(productEventRelation.getZbxId())) {
            List<DeviceEventRuleService.Triggers> triggers = JSONObject.parseArray(zbxTrigger.triggerGet(productEventRelation.getZbxId()), DeviceEventRuleService.Triggers.class);
            if (ToolUtil.isNotEmpty(triggers)) {
                zbxTrigger.triggerDelete(productEventRelation.getZbxId());
            }
        }


        //step 1:删除 与设备的关联
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
