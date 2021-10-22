package com.zmops.iot.web.device.controller;

import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.device.dto.DeviceLogDto;
import com.zmops.iot.web.device.dto.param.DeviceLogParam;
import com.zmops.iot.web.device.service.DeviceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseData list(@RequestParam(value = "deviceId") String deviceId,
                             @RequestParam(value = "logType", required = false) String logType,
                             @RequestParam(value = "timeFrom", required = false) Long timeFrom,
                             @RequestParam(value = "timeTill", required = false) Long timeTill) {

        return ResponseData.success(deviceLogService.list(deviceId, logType, timeFrom, timeTill));
    }

    @RequestMapping("getLogByPage")
    public Pager<DeviceLogDto> getLogByPage(@RequestBody DeviceLogParam deviceLogParam) {

        return deviceLogService.getLogByPage(deviceLogParam.getDeviceId(), deviceLogParam.getLogType(), deviceLogParam.getTimeFrom(),
                deviceLogParam.getTimeTill(),deviceLogParam.getContent(), deviceLogParam.getPage(), deviceLogParam.getMaxRow());
    }
}
