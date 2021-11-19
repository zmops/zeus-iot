package com.zmops.iot.web.device.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.Forest;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.event.applicationEvent.SceneEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yefei
 * <p>
 * 定时 触发场景 处理
 **/
@Slf4j
@Component
@EnableAsync
public class SceneScheduleProcessor {

    @Autowired
    DeviceLogService deviceLogService;

    @EventListener(classes = {SceneEvent.class})
    @Async
    public void subscribe(SceneEvent event) {
        log.info("子线程接收异步事件 - {}，String类型,执行顺序{}", event.getEventData().getExecuteParam(), 2);
        if (ToolUtil.isEmpty(event.getEventData().getExecuteParam())) {
            return;
        }
        Map<String, Object> eventMap = JSONObject.parseObject(event.getEventData().getExecuteParam(), Map.class);
        Long eventRuleId = Long.parseLong(eventMap.get("eventRuleId").toString());
        //记录日志
        deviceLogService.recordSceneLog(eventRuleId, "自动", null);

        //提交IOT SERVER 下发命令
        Forest.post("/device/action/exec").host("127.0.0.1").port(12800).contentTypeJson().addBody(JSON.toJSON(eventMap.get("executeParam"))).execute();
    }
}
