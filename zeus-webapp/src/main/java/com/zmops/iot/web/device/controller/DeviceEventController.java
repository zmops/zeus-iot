package com.zmops.iot.web.device.controller;

import com.zmops.iot.model.response.ResponseData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nantian created at 2021/9/20 14:30
 * <p>
 * 设备物模型： 事件
 */
@RestController
@RequestMapping("/device/event")
public class DeviceEventController {


    @RequestMapping("/create")
    public ResponseData createDeviceEvent() {
        return ResponseData.success();
    }
}
