package com.zmops.iot.web.device.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.Forest;
import com.google.common.eventbus.Subscribe;
import com.zmops.iot.async.executor.Async;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.service.work.DeviceServiceLogWorker;
import com.zmops.iot.web.device.service.work.ScenesLogWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * @author yefei
 **/
@Slf4j
@Service
public class SceneScheduleProcessor {

    @Autowired
    DeviceServiceLogWorker deviceServiceLogWorker;

    @Autowired
    ScenesLogWorker scenesLogWorker;

    @Subscribe
    public void subscribe(String event) {
        log.info("子线程接收异步事件 - {}，String类型", event);
        if(ToolUtil.isEmpty(event)){
            return;
        }
        Map<String,Object> eventMap = JSONObject.parseObject(event,Map.class);
        Long eventRuleId = Long.parseLong(eventMap.get("eventRuleId").toString());

        Map<String, Object> serviceLogInfo = new ConcurrentHashMap<>(3);
        serviceLogInfo.put("eventRuleId", eventRuleId);
        serviceLogInfo.put("triggerType", "场景联动");

        WorkerWrapper<Map<String, Object>, Boolean> deviceServiceLogWork = new WorkerWrapper.Builder<Map<String, Object>, Boolean>().id("deviceServiceLogWorker")
                .worker(deviceServiceLogWorker).param(serviceLogInfo)
                .build();

        Map<String, Object> sceneLogInfo = new ConcurrentHashMap<>(3);
        sceneLogInfo.put("eventRuleId", eventRuleId);
        sceneLogInfo.put("triggerType", "自动");

        WorkerWrapper<Map<String, Object>, Boolean> scenesLogWork = new WorkerWrapper.Builder<Map<String, Object>, Boolean>().id("scenesLogWorker")
                .worker(scenesLogWorker).param(sceneLogInfo)
                .build();

        try {
            Async.beginWork(1000, deviceServiceLogWork, scenesLogWork);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Forest.post("/device/action/exec").host("127.0.0.1").port(12800).contentTypeJson().addBody(JSON.toJSON(eventMap.get("executeParam"))).execute();
    }
}
