package com.zmops.iot.web.device.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.Forest;
import com.google.common.eventbus.Subscribe;
import com.zmops.iot.util.ToolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author yefei
 * <p>
 * 定时 触发场景 处理
 **/
@Slf4j
@Service
public class SceneScheduleProcessor {

    @Autowired
    DeviceLogService deviceLogService;

    @Subscribe
    public void subscribe(String event) {
        log.info("子线程接收异步事件 - {}，String类型", event);
        if (ToolUtil.isEmpty(event)) {
            return;
        }
        Map<String, Object> eventMap = JSONObject.parseObject(event, Map.class);
        Long eventRuleId = Long.parseLong(eventMap.get("eventRuleId").toString());
        //记录日志
        deviceLogService.recordSceneLog(eventRuleId, "自动", null);

        //提交IOT SERVER 下发命令
        Forest.post("/device/action/exec").host("127.0.0.1").port(12800).contentTypeJson().addBody(JSON.toJSON(eventMap.get("executeParam"))).execute();
    }
}
