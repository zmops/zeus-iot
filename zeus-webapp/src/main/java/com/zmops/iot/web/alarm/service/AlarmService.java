package com.zmops.iot.web.alarm.service;

import com.zmops.iot.domain.alarm.AlarmMessage;
import com.zmops.iot.mediaType.AlarmCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author yefei
 **/
@Service
public class AlarmService {

    @Autowired
    Collection<AlarmCallback> alarmCallbacks;


    public void test() {
        List<AlarmMessage> alarmMessages = new ArrayList<>();
        alarmMessages.add(AlarmMessage.builder().alarmMessage("DELTA 一级预警").build());
        alarmCallbacks.forEach(alarmCallback -> {
            if (alarmCallback.getType().equals("welink")) {
                alarmCallback.doAlarm(alarmMessages);
            }
        });
    }
}
