package com.zmops.iot.web.device.controller;

import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.device.dto.ServiceExecuteDto;
import com.zmops.iot.web.device.service.DeviceSvrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yefei
 **/
@RestController
@CrossOrigin
@RequestMapping("/device/service")
public class DeviceServiceController {

    @Autowired
    DeviceSvrService deviceSvrService;

    @RequestMapping("/execute")
    public ResponseData execute(@Validated @RequestBody ServiceExecuteDto serviceExecuteDto) {
        deviceSvrService.execute(serviceExecuteDto.getDeviceId(), serviceExecuteDto.getServiceId(), serviceExecuteDto.getServiceParams());
        return ResponseData.success();
    }
}
