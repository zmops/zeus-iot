package com.zmops.iot.rest;

import com.alibaba.fastjson.JSON;
import com.zmops.iot.async.executor.Async;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.product.ProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEventRelation;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.alarm.service.AlarmNoticeWorker;
import com.zmops.iot.web.alarm.service.AlarmService;
import com.zmops.iot.web.device.service.work.DeviceOnlineWorker;
import com.zmops.iot.web.device.service.work.DeviceServiceLogWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nantian created at 2021/8/7 14:56
 * <p>
 * 设备在线状态 回调接口
 */

@RestController
@RequestMapping("/device")
@Slf4j
public class DeviceStatusWebhookController {

    @Autowired
    private AlarmNoticeWorker alarmNoticeWorker;

    @Autowired
    DeviceOnlineWorker deviceOnlineWorker;

    @Autowired
    AlarmService alarmService;

    @Autowired
    DeviceServiceLogWorker deviceServiceLogWorker;

    /**
     * 在线状态 回调
     *
     * @param params webhook 回调参数
     * @return ResponseData
     */
    @RequestMapping("/status")
    public ResponseData deviceStatusWebhook(@RequestBody Map<String, String> params) {


        log.debug("--------设备上下线规则触发----------" + JSON.toJSONString(params));

        WorkerWrapper<Map<String, String>, Boolean> deviceOnlineWork = WorkerWrapper.<Map<String, String>, Boolean>builder().id("deviceOnlineWork")
                .worker(deviceOnlineWorker).param(params)
                .build();

        try {
            Async.work(3000, deviceOnlineWork).awaitFinish();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseData.success("OK");
    }

    /**
     * 设备告警 回调
     *
     * @param params webhook 回调参数
     * @return ResponseData
     */
    @RequestMapping("/problem")
    public ResponseData deviceProblemWebhook(@RequestBody Map<String, String> params) {


        log.debug("--------设备告警触发----------" + JSON.toJSONString(params));

        WorkerWrapper<Map<String, String>, Boolean> alarmNoticeWork = WorkerWrapper.<Map<String, String>, Boolean>builder().id("alarmNoticeWork")
                .worker(alarmNoticeWorker).param(params)
                .build();

        try {
            Async.work(3000, alarmNoticeWork).awaitFinish();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseData.success("OK");
    }

    /**
     * 设备服务 回调
     *
     * @param triggerId webhook 回调参数
     * @return ResponseData
     */
    @RequestMapping("/service")
    public ResponseData deviceServiceWebhook(@RequestParam("triggerId") String triggerId) {


        log.debug("--------设备联动触发----------" + triggerId);
        Map<String, Object> alarmInfo = new ConcurrentHashMap<>(3);

        ProductEventRelation productEventRelation = new QProductEventRelation().zbxId.eq(triggerId).findOne();
        if (productEventRelation == null) {
            return ResponseData.success("OK");
        }

        alarmInfo.put("eventRuleId", productEventRelation.getEventRuleId());
        alarmInfo.put("relationId", productEventRelation.getRelationId());

//        alarmService.action(alarmInfo);

        WorkerWrapper<Map<String, Object>, Boolean> deviceServiceLogWork = WorkerWrapper.<Map<String, Object>, Boolean>builder().id("deviceServiceLogWorker")
                .worker(deviceServiceLogWorker).param(alarmInfo)
                .build();

        try {
            Async.work(1000, deviceServiceLogWork).awaitFinish();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return ResponseData.success("OK");
    }

    /**
     * 设备服务 回调
     *
     * @param params webhook 回调参数
     * @return ResponseData
     */
    @RequestMapping("/event")
    public ResponseData deviceEventWebhook(@RequestBody Map<String, String> params) {


        log.debug("--------设备事件触发----------" + params);


        return ResponseData.success("OK");
    }
}
