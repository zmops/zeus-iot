package com.zmops.iot.web.event.service;

import com.zmops.iot.domain.messages.MessageBody;
import com.zmops.iot.domain.messages.NoticeRecord;
import com.zmops.iot.domain.messages.NoticeResult;
import com.zmops.iot.domain.product.ProductEvent;
import com.zmops.iot.domain.product.ProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEvent;
import com.zmops.iot.domain.product.query.QProductEventRelation;
import com.zmops.iot.domain.sys.SysUser;
import com.zmops.iot.domain.sys.query.QSysUser;
import com.zmops.iot.media.NoticeService;
import com.zmops.iot.util.DefinitionsUtil;
import com.zmops.iot.util.LocalDateTimeUtils;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.alarm.service.AlarmService;
import com.zmops.iot.web.alarm.service.MessageService;
import com.zmops.iot.web.event.EventProcess;
import com.zmops.iot.web.event.dto.EventDataDto;
import com.zmops.iot.web.sys.dto.UserGroupDto;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
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


    @Autowired
    NoticeService noticeService;

    @Override
    public void process(EventDataDto eventData) {
        String triggerId = eventData.getObjectid();
        String triggerName = eventData.getName();

        log.debug("--------alarm event----------{}", triggerId);

        List<ProductEventRelation> list = new QProductEventRelation().zbxId.eq(triggerId).findList();
        if(ToolUtil.isEmpty(list)){
            return;
        }
        Map<String, Object> params = new ConcurrentHashMap<>(2);
        List<String> deviceIds = list.parallelStream().map(ProductEventRelation::getRelationId).collect(Collectors.toList());
        params.put("hostname", deviceIds);
        params.put("triggerName", triggerName);

        List<Long> userIds = new QSysUser().select(QSysUser.alias().userId).findSingleAttributeList();

        messageService.push(buildMessage(params, userIds));
        alarmService.alarm(params);

        //发送消息
        ProductEvent productEvent = new QProductEvent().eventRuleId.eq(Long.parseLong(triggerName)).findOne();
        Map<String, String> macros = createMacroMap(triggerId,eventData.getRecoveryValue(),eventData.getAcknowledged(),productEvent);

        String sql = "select user_group_id from sys_usrgrp_devicegrp where device_group_id in (select device_group_id from devices_groups where device_id in (:deviceIds))";
        List<UserGroupDto> userGroups = DB.findNative(UserGroupDto.class, sql).setParameter("deviceIds", deviceIds).findList();
        QSysUser qSysUser = new QSysUser();

        if (ToolUtil.isNotEmpty(userGroups)) {
            qSysUser.userGroupId.in(userGroups.parallelStream().map(UserGroupDto::getUserGroupId).collect(Collectors.toList()));
        }
        List<SysUser> sysUserList = qSysUser.findList();
        List<NoticeRecord> noticeRecords = new ArrayList<>();
        sysUserList.forEach(sysUser -> {
            Map<Integer, NoticeResult> notice = noticeService.notice(sysUser, macros,triggerId);
            for (Map.Entry<Integer, NoticeResult> en : notice.entrySet()) {
                noticeRecords.add(NoticeRecord.builder()
                        .userId(sysUser.getUserId())
                        .problemId(triggerId)
                        .noticeType(en.getKey())
                        .noticeStatus(en.getValue().getStatus().name())
                        .noticeMsg(en.getValue().getMsg())
                        .creatTime(LocalDateTime.now())
                        .alarmInfo(en.getValue().getAlarmInfo())
                        .receiveAccount(en.getValue().getReceiveAccount())
                        .build());
            }
        });
        DB.saveAll(noticeRecords);
    }

    private Map<String, String> createMacroMap(String triggerId,String value,String acknowledged,ProductEvent productEvent) {
        boolean isnew = "1".equals(value);
        Map<String, String> macroMap = new HashMap<String, String>();
        macroMap.put("${time}", LocalDateTimeUtils.formatTime(LocalDateTime.now()));

        macroMap.put("${level}", DefinitionsUtil.getNameByVal("EVENT_LEVEL", productEvent.getEventLevel()));
        macroMap.put("${severity}", productEvent.getEventLevel());
        macroMap.put("${metricName}", productEvent.getEventRuleName());
        macroMap.put("${context}", productEvent.getEventRuleName());
        macroMap.put("${alarmStatus}", isnew ? "告警触发" : "撤销告警");
        macroMap.put("${confirmStatus}", "1".equals(acknowledged) ? "已确认" : "未确认");
        macroMap.put("${problemId}", triggerId);
        return macroMap;
    }

    private MessageBody buildMessage(Map<String, Object> alarmInfo, List<Long> userIds) {

        return MessageBody.builder().msg("告警消息").persist(true).to(userIds).body(alarmInfo).build();
    }


    @Override
    public boolean checkTag(String tag) {
        return "__alarm__".equals(tag);
    }
}
