package com.zmops.iot.web.alarm.service;

import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.messages.MessageBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yefei
 **/
@Service
public class AlarmNoticeWorker implements IWorker<Map<String, String>, Boolean> {

    @Autowired
    MessageService messageService;

    @Autowired
    AlarmService alarmService;

    @Override
    public Boolean action(Map<String, String> alarmInfo, Map<String, WorkerWrapper<?, ?>> allWrappers) {
        Map<String, Object> alarmInfo2 = new HashMap<>(alarmInfo);
        messageService.push(buildMessage(alarmInfo2));
        alarmService.alarm(alarmInfo);
        return null;
    }

    private MessageBody buildMessage(Map<String, Object> alarmInfo) {

        return MessageBody.builder().msg("告警消息").persist(true).body(alarmInfo).build();
    }


}
