package com.zmops.iot.web.event.service;

import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.messages.MessageBody;
import com.zmops.iot.domain.product.ProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEventRelation;
import com.zmops.iot.domain.sys.query.QSysUser;
import com.zmops.iot.web.alarm.service.AlarmService;
import com.zmops.iot.web.alarm.service.MessageService;
import com.zmops.iot.web.event.EventProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yefei
 **/
@Slf4j
@Component
public class AlarmEventProcess implements EventProcess {

    @Autowired
    MessageService messageService;

    @Autowired
    AlarmService alarmService;

    @Override
    public void process(String triggerId,String triggerName) {
        log.debug("--------alarm event----------{}" , triggerId);

        List<ProductEventRelation> list = new QProductEventRelation().zbxId.eq(triggerId).findList();
        Map<String, Object> params = new ConcurrentHashMap<>(2);
        List<String> deviceIds = list.parallelStream().map(ProductEventRelation::getRelationId).collect(Collectors.toList());
        params.put("hostname", deviceIds);
        params.put("triggerName", triggerName);

        List<Long> userIds = new QSysUser().select(QSysUser.alias().userId).findSingleAttributeList();

        messageService.push(buildMessage(params, userIds));
        alarmService.alarm(params);
    }

    private MessageBody buildMessage(Map<String, Object> alarmInfo, List<Long> userIds) {

        return MessageBody.builder().msg("告警消息").persist(true).to(userIds).body(alarmInfo).build();
    }


    @Override
    public boolean checkTag(String tag) {
        return "__alarm__".equals(tag);
    }
}
