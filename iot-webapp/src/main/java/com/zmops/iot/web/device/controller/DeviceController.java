package com.zmops.iot.web.device.controller;

import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.iot.web.device.dto.param.DeviceParam;
import com.zmops.iot.web.device.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nantian created at 2021/8/2 2:05
 * <p>
 * 设备管理
 */
@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    DeviceService deviceService;

    /**
     * 设备分页列表
     *
     * @return
     */
    @RequestMapping("/getDeviceByPage")
    public ResponseData devicePageList(@RequestBody DeviceParam deviceParam) {
        return ResponseData.success(deviceService.devicePageList(deviceParam));
    }







}
