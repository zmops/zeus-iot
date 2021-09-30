package com.zmops.iot.web.alarm.controller;

import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.alarm.dto.AlarmDto;
import com.zmops.iot.web.alarm.dto.param.AlarmParam;
import com.zmops.iot.web.alarm.service.AlarmService;
import com.zmops.iot.web.auth.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public Pager<AlarmDto> getAlarmByPage(@RequestBody AlarmParam alarmParam) {
        return alarmService.getAlarmByPage(alarmParam);
    }

    @Permission(code = "alarmList")
    @RequestMapping("/acknowledgement")
    public ResponseData acknowledgement(@RequestParam String eventId) {
        alarmService.acknowledgement(eventId);
        return ResponseData.success();
    }


    @Permission(code = "alarmList")
    @RequestMapping("/resolve")
    public ResponseData resolve(@RequestParam String eventId) {
        alarmService.resolve(eventId);
        return ResponseData.success();
    }

}
