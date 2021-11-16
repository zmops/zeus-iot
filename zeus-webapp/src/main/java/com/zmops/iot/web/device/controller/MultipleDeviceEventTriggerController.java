package com.zmops.iot.web.device.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.core.auth.context.LoginContextHolder;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.product.ProductEvent;
import com.zmops.iot.domain.product.ProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEvent;
import com.zmops.iot.domain.product.query.QProductEventExpression;
import com.zmops.iot.domain.product.query.QProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEventService;
import com.zmops.iot.domain.schedule.Task;
import com.zmops.iot.domain.schedule.query.QTask;
import com.zmops.iot.enums.CommonStatus;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.MultipleDeviceEventDto;
import com.zmops.iot.web.device.dto.MultipleDeviceEventRule;
import com.zmops.iot.web.device.dto.param.MultipleDeviceEventParm;
import com.zmops.iot.web.device.service.MultipleDeviceEventRuleService;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.zeus.driver.service.ZbxTrigger;
import io.ebean.DB;
import io.ebean.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.zmops.iot.web.device.service.MultipleDeviceEventRuleService.TRIGGER_TYPE_CONDITION;
import static com.zmops.iot.web.device.service.MultipleDeviceEventRuleService.TRIGGER_TYPE_SCHEDULE;

/**
 * @author yefei
 * <p>
 * 设备联动
 **/
@RestController
@RequestMapping("/multiple/device/event/trigger")
public class MultipleDeviceEventTriggerController {

    @Autowired
    MultipleDeviceEventRuleService multipleDeviceEventRuleService;

    @Autowired
    private ZbxTrigger zbxTrigger;

    private static final String EXECUTE_TAG_NAME = "__scene__";

    /**
     * 场景 分页列表
     *
     * @param eventParm
     * @return
     */
    @PostMapping("/getEventByPage")
    public Pager<MultipleDeviceEventDto> getEventByPage(@RequestBody MultipleDeviceEventParm eventParm) {
        return multipleDeviceEventRuleService.getEventByPage(eventParm);
    }

    /**
     * 场景 列表
     *
     * @param eventParm
     * @return
     */
    @PostMapping("/list")
    public ResponseData list(@RequestBody MultipleDeviceEventParm eventParm) {
        return ResponseData.success(multipleDeviceEventRuleService.list(eventParm));
    }

    /**
     * 设备联动 详情
     *
     * @param eventRuleId
     * @return
     */

    @GetMapping("/detail")
    public ResponseData detail(@RequestParam("eventRuleId") long eventRuleId) {
        ProductEvent productEvent = new QProductEvent().eventRuleId.eq(eventRuleId).findOne();
        if (null == productEvent) {
            throw new ServiceException(BizExceptionEnum.EVENT_NOT_EXISTS);
        }
        return ResponseData.success(multipleDeviceEventRuleService.detail(productEvent, eventRuleId));
    }

    /**
     * 创建 设备联动
     *
     * @param eventRule 设备联动规则
     * @return 设备联动ID
     */
    @Transactional
    @PostMapping("/create")
    public ResponseData createDeviceEventRule(@RequestBody @Validated(value = BaseEntity.Create.class)
                                                      MultipleDeviceEventRule eventRule) {

        multipleDeviceEventRuleService.checkParam(eventRule);

        Long eventRuleId = IdUtil.getSnowflake().nextId();

        eventRule.setEventRuleId(eventRuleId);
        multipleDeviceEventRuleService.createDeviceEventRule(eventRule);

        if (TRIGGER_TYPE_CONDITION == eventRule.getTriggerType()) {
            //step 1: 先创建 zbx 触发器
            String expression = eventRule.getExpList()
                    .stream().map(Object::toString).collect(Collectors.joining(" " + eventRule.getExpLogic() + " "));

            //step 2: zbx 保存触发器
            String[] triggerIds = multipleDeviceEventRuleService.createZbxTrigger(eventRuleId + "", expression, eventRule.getEventLevel());

            //step 4: zbx 触发器创建 Tag
            Map<String, String> tags = new ConcurrentHashMap<>(1);
            if (ToolUtil.isNotEmpty(eventRule.getTags())) {
                tags = eventRule.getTags().stream()
                        .collect(Collectors.toMap(MultipleDeviceEventRule.Tag::getTag, MultipleDeviceEventRule.Tag::getValue, (k1, k2) -> k2));
            }

            if (ToolUtil.isNotEmpty(eventRule.getDeviceServices()) && !tags.containsKey(EXECUTE_TAG_NAME)) {
                tags.put(EXECUTE_TAG_NAME, eventRuleId + "");
            }

            for (String triggerId : triggerIds) {
                zbxTrigger.triggerTagCreate(triggerId, tags);
            }

            //step 5: 更新 zbxId
            multipleDeviceEventRuleService.updateProductEventRuleZbxId(eventRuleId, triggerIds);
        }
        // 返回触发器ID
        return ResponseData.success(eventRuleId);
    }

    /**
     * 修改 设备联动 状态
     *
     * @param eventRule 设备联动规则
     */
    @PostMapping("/status")
    public ResponseData updateProductEventStatus(@RequestBody @Validated(value = BaseEntity.Status.class) MultipleDeviceEventRule eventRule) {

        ProductEvent productEvent = new QProductEvent().eventRuleId.eq(eventRule.getEventRuleId()).findOne();
        if (null == productEvent) {
            throw new ServiceException(BizExceptionEnum.SCENE_NOT_EXISTS);
        }

        if (TRIGGER_TYPE_SCHEDULE == productEvent.getTriggerType()) {
            DB.update(Task.class).where().eq("taskId", productEvent.getTaskId()).asUpdate()
                    .set("triggerStatus", eventRule.getStatus()).update();
        } else {

            DB.update(ProductEventRelation.class).where().eq("eventRuleId", eventRule.getEventRuleId()).asUpdate()
                    .set("status", eventRule.getStatus()).update();

            ProductEventRelation productEventRelation = new QProductEventRelation().eventRuleId.eq(eventRule.getEventRuleId()).findOne();

            if (null != productEventRelation && null != productEventRelation.getZbxId()) {
                zbxTrigger.triggerStatusUpdate(productEventRelation.getZbxId(), eventRule.getStatus().equals(CommonStatus.ENABLE.getCode()) ? "0" : "1");
            }
        }

        return ResponseData.success();
    }

    /**
     * 修改 设备联动
     *
     * @param eventRule 设备联动规则
     * @return 设备联动ID
     */
    @Transactional
    @PostMapping("/update")
    public ResponseData updateDeviceEventRule(@RequestBody @Validated(value = BaseEntity.Update.class) MultipleDeviceEventRule eventRule) {
        ProductEvent productEvent = new QProductEvent().eventRuleId.eq(eventRule.getEventRuleId())
                .findOne();
        if (null == productEvent) {
            throw new ServiceException(BizExceptionEnum.EVENT_NOT_EXISTS);
        }

        //检查参数
        multipleDeviceEventRuleService.checkParam(eventRule);

        //step 1: 删除原有的 关联关系 并重新建立 关联关系
        ProductEventRelation productEventRelation = new QProductEventRelation().eventRuleId.eq(eventRule.getEventRuleId()).setMaxRows(1).findOne();
        eventRule.setTaskId(productEvent.getTaskId());
        if (null != productEventRelation) {
            eventRule.setZbxId(productEventRelation.getZbxId());
        }
        multipleDeviceEventRuleService.updateDeviceEventRule(eventRule);

        if (TRIGGER_TYPE_CONDITION == eventRule.getTriggerType()) {

            //step 1: 先创建 zbx 触发器
            String expression = eventRule.getExpList()
                    .stream().map(Object::toString).collect(Collectors.joining(" " + eventRule.getExpLogic() + " "));

            //step 2: zbx 保存触发器
            String[] triggerIds = multipleDeviceEventRuleService.createZbxTrigger(eventRule.getEventRuleId() + "", expression, eventRule.getEventLevel());

            //step 4: zbx 触发器创建 Tag
            Map<String, String> tags = new ConcurrentHashMap<>(1);
            if (ToolUtil.isNotEmpty(eventRule.getTags())) {
                tags = eventRule.getTags().stream()
                        .collect(Collectors.toMap(MultipleDeviceEventRule.Tag::getTag, MultipleDeviceEventRule.Tag::getValue, (k1, k2) -> k2));
            }

            if (ToolUtil.isNotEmpty(eventRule.getDeviceServices()) && !tags.containsKey(EXECUTE_TAG_NAME)) {
                tags.put(EXECUTE_TAG_NAME, eventRule.getEventRuleId() + "");
            }

            for (String triggerId : triggerIds) {
                zbxTrigger.triggerTagCreate(triggerId, tags);
            }

            //step 5: 更新 zbxId 反写
            multipleDeviceEventRuleService.updateProductEventRuleZbxId(eventRule.getEventRuleId(), triggerIds);
        }
        // 返回触发器ID
        return ResponseData.success(eventRule.getEventRuleId());
    }


    /**
     * 删除 设备联动
     *
     * @param eventRule 设备联动规则
     */
    @Transactional
    @PostMapping("/delete")
    public ResponseData deleteProductEventRule(@RequestBody @Validated(value = BaseEntity.Delete.class) MultipleDeviceEventRule eventRule) {

        ProductEvent productEvent = new QProductEvent().eventRuleId.eq(eventRule.getEventRuleId()).findOne();
        if (null == productEvent) {
            throw new ServiceException(BizExceptionEnum.SCENE_NOT_EXISTS);
        }
        if (TRIGGER_TYPE_CONDITION == eventRule.getTriggerType()) {
            List<ProductEventRelation> productEventRelationList = new QProductEventRelation()
                    .eventRuleId.eq(eventRule.getEventRuleId()).findList();

            //step 01:删除 zbx触发器
            if (ToolUtil.isNotEmpty(productEventRelationList) && ToolUtil.isNotEmpty(productEventRelationList.get(0).getZbxId())) {
                List<MultipleDeviceEventRuleService.Triggers> triggers = JSONObject.parseArray(
                        zbxTrigger.triggerGet(productEventRelationList.get(0).getZbxId()), MultipleDeviceEventRuleService.Triggers.class);

                if (ToolUtil.isNotEmpty(triggers)) {
                    zbxTrigger.triggerDelete(productEventRelationList.get(0).getZbxId());
                }
            }

            //step 1:删除 与设备的关联
            new QProductEventRelation().eventRuleId.eq(eventRule.getEventRuleId()).delete();

            //step 2:删除 关联的表达式
            new QProductEventExpression().eventRuleId.eq(eventRule.getEventRuleId()).delete();

        } else {
            //step 1:删除 定时器
            new QTask().id.eq(productEvent.getTaskId()).delete();
        }

        //step 3:删除 关联的执行服务
        new QProductEventService().eventRuleId.eq(eventRule.getEventRuleId()).delete();

        //step 4:删除 触发器
        new QProductEvent().eventRuleId.eq(eventRule.getEventRuleId()).delete();

        return ResponseData.success();
    }

    @RequestMapping("/execute")
    public ResponseData execute(@RequestParam("eventRuleId") Long eventRuleId) {
        int count = new QProductEvent().eventRuleId.eq(eventRuleId).classify.eq("1").findCount();
        if (count <= 0) {
            throw new ServiceException(BizExceptionEnum.SCENE_NOT_EXISTS);
        }
        multipleDeviceEventRuleService.execute(eventRuleId, "手动", LoginContextHolder.getContext().getUser().getId());
        return ResponseData.success();
    }

}
