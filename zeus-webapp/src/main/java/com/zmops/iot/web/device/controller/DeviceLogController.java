package com.zmops.iot.web.device.controller;

import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.device.service.DeviceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yefei
 **/
@RestController
@RequestMapping("/device/log")
public class DeviceLogController {

    @Autowired
    DeviceLogService deviceLogService;

    @RequestMapping("list")
    public ResponseData getLogByPage(@RequestParam(value = "deviceId") String deviceId,
                                     @RequestParam(value = "logType", required = false) String logType,
                                     @RequestParam(value = "timeFrom", required = false) Long timeFrom,
                                     @RequestParam(value = "timeTill", required = false) Long timeTill) {

        return ResponseData.success(deviceLogService.getLogByPage(deviceId, logType, timeFrom, timeTill));
    }
}
