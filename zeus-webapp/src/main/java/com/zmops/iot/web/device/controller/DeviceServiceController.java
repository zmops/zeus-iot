package com.zmops.iot.web.device.controller;

import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.device.service.DeviceSvrService;
import org.apache.camel.spi.AsEndpointUri;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseData execute(@RequestParam("deviceId") String deviceId,@RequestParam("serviceId") Long serviceId){
        deviceSvrService.execute(deviceId,serviceId);
        return ResponseData.success();
    }
}
