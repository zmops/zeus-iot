package com.zmops.iot.web.device.controller;

import cn.hutool.core.util.IdUtil;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.product.ProductEvent;
import com.zmops.iot.domain.product.query.QProductEvent;
import com.zmops.iot.domain.product.query.QProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEventService;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.service.DeviceEventService;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductEventRule;
import com.zmops.zeus.driver.service.ZbxTrigger;
import io.ebean.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 设备告警规则
 **/
@RestController
@RequestMapping("/device/event/trigger")
public class DeviceEventController {

    @Autowired
    DeviceEventService deviceEventService;

    @Autowired
    private ZbxTrigger zbxTrigger;

    private static final String ALARM_TAG_NAME   = "__alarm__";
    private static final String EXECUTE_TAG_NAME = "__execute__";

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
        return ResponseData.success(deviceEventService.detail(productEvent, eventRuleId, deviceId));
    }

    /**
     * 修改 触发器
     *
     * @param eventRule 触发器规则
     * @return 触发器ID
     */
    @Transactional
    @PostMapping("/update")
    public ResponseData updateDeviceEventRule(@RequestBody @Validated(value = BaseEntity.Update.class)
                                                      ProductEventRule eventRule) {

        List<String> deviceIds = eventRule.getExpList().parallelStream().map(ProductEventRule.Expression::getDeviceId)
                .collect(Collectors.toList());
        if (ToolUtil.isEmpty(deviceIds)) {
            throw new ServiceException(BizExceptionEnum.EVENT_HAS_NOT_DEVICE);
        }
        //step 1: 删除原有的 关联关系
        deviceIds.forEach(deviceId -> {
            new QProductEventRelation().eventRuleId.eq(eventRule.getEventRuleId()).relationId.eq(deviceId).delete();
            new QProductEventService().eventRuleId.eq(eventRule.getEventRuleId()).deviceId.eq(deviceId).delete();
        });


        Long eventRuleId = IdUtil.getSnowflake().nextId(); // ruleId, trigger name

        deviceEventService.createProductEventRule(eventRuleId, eventRule);

        //step 1: 先创建 zbx 触发器
        String expression = eventRule.getExpList()
                .stream().map(Object::toString).collect(Collectors.joining(" " + eventRule.getExpLogic() + " "));

        //step 2: zbx 保存触发器
        String[] triggerIds = deviceEventService.createZbxTrigger(eventRuleId + "", expression, eventRule.getEventLevel());

        //step 4: zbx 触发器创建 Tag
        Map<String, String> tags = eventRule.getTags().stream()
                .collect(Collectors.toMap(ProductEventRule.Tag::getTag, ProductEventRule.Tag::getValue, (k1, k2) -> k2));
        if (ToolUtil.isEmpty(tags)) {
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
        deviceEventService.updateProductEventRuleZbxId(eventRuleId, triggerIds);

        // 返回触发器ID
        return ResponseData.success(eventRuleId);
    }

}
