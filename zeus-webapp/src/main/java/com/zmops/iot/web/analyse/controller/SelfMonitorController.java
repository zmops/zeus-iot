package com.zmops.iot.web.analyse.controller;

import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.analyse.service.SelfMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("memory")
    public ResponseData getMemInfo(){
        return ResponseData.success(selfMonitorService.getMemInfo());
    }

    @GetMapping("cpuLoad")
    public ResponseData getCpuInfo(){
        return ResponseData.success(selfMonitorService.getCpuLoadInfo());
    }

    @GetMapping("cpuUtilization")
    public ResponseData getCpuUtilization(){
        return ResponseData.success(selfMonitorService.getCpuUtilization());
    }

    @RequestMapping("process")
    public ResponseData getProcessInfo(){
        return ResponseData.success(selfMonitorService.getProcessInfo());
    }


}
