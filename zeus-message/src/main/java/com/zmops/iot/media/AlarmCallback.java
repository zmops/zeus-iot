package com.zmops.iot.media;

import com.zmops.iot.domain.alarm.AlarmMessage;

import java.util.List;

/**
 * Alarm call back will be called by alarm implementor, after it decided alarm should be sent.
 */
public interface AlarmCallback {

    void doAlarm(List<AlarmMessage> alarmMessage);

    String getType();
}
