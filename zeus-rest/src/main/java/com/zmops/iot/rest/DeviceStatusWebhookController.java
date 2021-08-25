package com.zmops.iot.rest;

import com.alibaba.fastjson.JSON;
import com.zmops.iot.async.executor.Async;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.alarm.service.AlarmNoticeWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author nantian created at 2021/8/7 14:56
 * <p>
 * 设备在线状态 回调接口
 */

@RestController
@RequestMapping("/rest/device")
public class DeviceStatusWebhookController {

    @Autowired
    private AlarmNoticeWorker alarmNoticeWorker;

    /**
     * 在线状态 回调
     *
     * @param params webhook 回调参数
     * @return ResponseData
     */
    @RequestMapping("/webhook")
    public ResponseData deviceStatusWebhook(@RequestBody Map<String, String> params) {


        System.out.println(JSON.toJSONString(params));

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
}
