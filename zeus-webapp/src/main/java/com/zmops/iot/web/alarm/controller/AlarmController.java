package com.zmops.iot.web.alarm.controller;

import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.alarm.dto.param.AlarmParam;
import com.zmops.iot.web.alarm.service.AlarmService;
import com.zmops.iot.web.auth.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yefei
 **/
@RestController
@RequestMapping("/alarm")
public class AlarmController {

    @Autowired
    AlarmService alarmService;

    //    @RequestMapping("/test")
//    public void test() {
//        alarmService.test();
//    }
    @Permission(code = "alarmList")
    @RequestMapping("/getAlarmByPage")
    public ResponseData getAlarmByPage(@RequestBody AlarmParam alarmParam) {
        return ResponseData.success(alarmService.getAlarmByPage(alarmParam));
    }
}
