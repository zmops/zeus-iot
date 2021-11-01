package com.zmops.iot.web.event.service;

import com.zmops.iot.domain.alarm.Problem;
import com.zmops.iot.domain.messages.MessageBody;
import com.zmops.iot.domain.messages.NoticeRecord;
import com.zmops.iot.domain.messages.NoticeResult;
import com.zmops.iot.domain.product.ProductEvent;
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

        //step 1:插入problem
        Problem problem = new Problem();
        problem.setEventId(Long.parseLong(eventData.getEventid()));
        problem.setObjectId(Long.parseLong(eventData.getObjectid()));
        problem.setAcknowledged(eventData.getAcknowledged());
        problem.setSeverity(eventData.getSeverity());
        problem.setName(DefinitionsUtil.getTriggerName(Long.parseLong(eventData.getName())));
        problem.setDeviceId(eventData.getTagValue());
        problem.setClock(LocalDateTimeUtils.getLDTBySeconds(eventData.getClock()));
        problem.setRClock(eventData.getRClock() == 0 ? null : LocalDateTimeUtils.getLDTBySeconds(eventData.getRClock()));
        if (eventData.getRClock() == 0 && eventData.getAcknowledged() == 0) {
            DB.insert(problem);
        } else {
            DB.update(problem);
        }

        //step 2:找出需要通知的用户ID 推送通知
        List<String> deviceIds = new QProductEventRelation().select(QProductEventRelation.alias().relationId).zbxId.eq(triggerId).findSingleAttributeList();
        if (ToolUtil.isEmpty(deviceIds)) {
            return;
        }
        Map<String, Object> params = new ConcurrentHashMap<>(2);
        params.put("hostname", deviceIds);
        params.put("triggerName", triggerName);

        String sql = "select user_group_id from sys_usrgrp_devicegrp where device_group_id in (select device_group_id from devices_groups where device_id in (:deviceIds))";
        List<UserGroupDto> userGroups = DB.findNative(UserGroupDto.class, sql).setParameter("deviceIds", deviceIds).findList();

        QSysUser qSysUser = new QSysUser();
        if (ToolUtil.isNotEmpty(userGroups)) {
            qSysUser.userGroupId.in(userGroups.parallelStream().map(UserGroupDto::getUserGroupId).collect(Collectors.toList()));
        }
        List<SysUser> sysUserList = qSysUser.findList();
        List<Long> userIds = sysUserList.parallelStream().map(SysUser::getUserId).collect(Collectors.toList());

        messageService.push(buildMessage(params, userIds));
        alarmService.alarm(params);

        //发送Email消息
        ProductEvent productEvent = new QProductEvent().eventRuleId.eq(Long.parseLong(triggerName)).findOne();
        Map<String, String> macros = createMacroMap(triggerId, eventData.getRClock() + "", eventData.getAcknowledged() + "", productEvent);

        List<NoticeRecord> noticeRecords = new ArrayList<>();
        sysUserList.forEach(sysUser -> {
            Map<Integer, NoticeResult> notice = noticeService.notice(sysUser, macros, triggerId);
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

    private Map<String, String> createMacroMap(String triggerId, String rclock, String acknowledged, ProductEvent productEvent) {
        boolean isnew = "0".equals(rclock);
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
