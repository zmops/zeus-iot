package com.zmops.iot.web.analyse.controller;

import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.analyse.service.SelfMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yefei
 * 自监控
 **/
@RestController
@RequestMapping("/monitor/self")
public class SelfMonitorController {

    @Autowired
    SelfMonitorService selfMonitorService;

    @RequestMapping("memory")
    public ResponseData getMemInfo(){
        return ResponseData.success(selfMonitorService.getMemInfo());
    }

    @RequestMapping("cpu")
    public ResponseData getCpuInfo(){
        return ResponseData.success(selfMonitorService.getCpuInfo());
    }

    @RequestMapping("process")
    public ResponseData getProcessInfo(){
        return ResponseData.success(selfMonitorService.getProcessInfo());
    }


}
