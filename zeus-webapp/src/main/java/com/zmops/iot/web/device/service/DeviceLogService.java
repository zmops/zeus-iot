package com.zmops.iot.web.device.service;


import com.zmops.iot.core.auth.context.LoginContextHolder;
import com.zmops.iot.domain.device.EventTriggerRecord;
import com.zmops.iot.domain.device.ScenesTriggerRecord;
import com.zmops.iot.domain.device.ServiceExecuteRecord;
import com.zmops.iot.domain.device.query.QEventTriggerRecord;
import com.zmops.iot.domain.device.query.QScenesTriggerRecord;
import com.zmops.iot.domain.device.query.QServiceExecuteRecord;
import com.zmops.iot.domain.product.query.QProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEventService;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.DefinitionsUtil;
import com.zmops.iot.util.LocalDateTimeUtils;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.alarm.dto.AlarmDto;
import com.zmops.iot.web.alarm.dto.param.AlarmParam;
import com.zmops.iot.web.alarm.service.AlarmService;
import com.zmops.iot.web.device.dto.DeviceLogDto;
import com.zmops.iot.web.device.dto.DeviceRelationDto;
import com.zmops.iot.web.device.dto.param.DeviceLogParam;
import com.zmops.iot.web.device.service.work.DeviceServiceLogWorker;
import com.zmops.iot.web.device.service.work.ScenesLogWorker;
import com.zmops.zeus.server.async.callback.ICallback;
import com.zmops.zeus.server.async.executor.Async;
import com.zmops.zeus.server.async.wrapper.WorkerWrapper;
import io.ebean.DB;
import io.ebean.PagedList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author yefei
 **/
@Service
public class DeviceLogService {

    private static final String LOG_TYPE_ALARM   = "告警日志";
    private static final String LOG_TYPE_EVENT   = "事件日志";
    private static final String LOG_TYPE_SERVICE = "服务日志";
    private static final String LOG_TYPE_SCENES  = "联动日志";

    @Autowired
    AlarmService alarmService;

    @Autowired
    DeviceServiceLogWorker deviceServiceLogWorker;

    @Autowired
    ScenesLogWorker scenesLogWorker;

    public List<DeviceLogDto> list(String deviceId, String logType, Long timeFrom, Long timeTill) {
        List<DeviceLogDto> deviceLogDtoList = new ArrayList<>();
        if (ToolUtil.isEmpty(logType) || (ToolUtil.isNotEmpty(logType) && LOG_TYPE_ALARM.equals(logType))) {
            AlarmParam alarmParam = new AlarmParam();
            alarmParam.setDeviceId(deviceId);
            alarmParam.setTimeFrom(timeFrom);
            alarmParam.setTimeTill(timeTill);
            List<AlarmDto> alarmList = alarmService.getAlarmList(alarmParam);
            if (ToolUtil.isNotEmpty(alarmList)) {
                alarmList.forEach(alarm -> {
                    deviceLogDtoList.add(DeviceLogDto.builder().logType(LOG_TYPE_ALARM).content(alarm.getName())
                            .triggerTime(LocalDateTimeUtils.formatTime(alarm.getClock()))
                            .status(null == alarm.getRClock() ? "未解决" : "已解决").build());
                });
            }
        }

        if (ToolUtil.isEmpty(logType) || (ToolUtil.isNotEmpty(logType) && LOG_TYPE_EVENT.equals(logType))) {
            QEventTriggerRecord query = new QEventTriggerRecord().deviceId.eq(deviceId);
            if (null != timeFrom) {
                query.createTime.ge(LocalDateTimeUtils.getLDTByMilliSeconds(timeFrom * 1000));
            }
            if (null != timeTill) {
                query.createTime.lt(LocalDateTimeUtils.getLDTByMilliSeconds(timeTill * 1000));
            }
            List<EventTriggerRecord> list = query.findList();
            if (ToolUtil.isNotEmpty(list)) {
                list.forEach(service -> {
                    deviceLogDtoList.add(DeviceLogDto.builder().logType(LOG_TYPE_EVENT).content(service.getEventName())
                            .triggerTime(LocalDateTimeUtils.formatTime(service.getCreateTime()))
                            .param(service.getEventName()).build());
                });
            }
        }

        if (ToolUtil.isEmpty(logType) || (ToolUtil.isNotEmpty(logType) && LOG_TYPE_SERVICE.equals(logType))) {
            QServiceExecuteRecord query = new QServiceExecuteRecord().deviceId.eq(deviceId);
            if (null != timeFrom) {
                query.createTime.ge(LocalDateTimeUtils.getLDTByMilliSeconds(timeFrom * 1000));
            }
            if (null != timeTill) {
                query.createTime.lt(LocalDateTimeUtils.getLDTByMilliSeconds(timeTill * 1000));
            }
            List<ServiceExecuteRecord> list = query.findList();
            if (ToolUtil.isNotEmpty(list)) {
                list.forEach(service -> {
                    deviceLogDtoList.add(DeviceLogDto.builder().logType(LOG_TYPE_SERVICE).content(service.getServiceName())
                            .triggerTime(LocalDateTimeUtils.formatTime(service.getCreateTime()))
                            .param(service.getParam()).build());
                });
            }
        }
        deviceLogDtoList.sort(Comparator.comparing(DeviceLogDto::getTriggerTime));
        return deviceLogDtoList;
    }


    public Pager<DeviceLogDto> getLogByPage(DeviceLogParam deviceLogParam) {

        String logType = deviceLogParam.getLogType();

        if (ToolUtil.isNotEmpty(logType) && LOG_TYPE_SERVICE.equals(logType)) {

            return getServiceLog(deviceLogParam);

        } else if (ToolUtil.isNotEmpty(logType) && LOG_TYPE_SCENES.equals(logType)) {

            return getScenesLog(deviceLogParam);

        } else {

            return getEventLog(deviceLogParam);

        }

    }

    /**
     * 告警日志
     *
     * @param deviceId
     * @param timeFrom
     * @param timeTill
     * @param content
     * @return
     */
    private List<DeviceLogDto> getAlarmLog(String deviceId, Long timeFrom, Long timeTill, String content) {
        List<DeviceLogDto> deviceLogDtoList = new ArrayList<>();
        AlarmParam alarmParam = new AlarmParam();
        if (ToolUtil.isNotEmpty(deviceId)) {
            alarmParam.setDeviceId(deviceId);
        }
        alarmParam.setTimeFrom(timeFrom);
        alarmParam.setTimeTill(timeTill);
        List<AlarmDto> alarmList = alarmService.getAlarmList(alarmParam);
        if (ToolUtil.isNotEmpty(alarmList)) {
            alarmList.forEach(alarm -> {
                deviceLogDtoList.add(DeviceLogDto.builder().logType(LOG_TYPE_ALARM).content(alarm.getName())
                        .triggerTime(LocalDateTimeUtils.formatTime(alarm.getClock()))
                        .status(null == alarm.getRClock() ? "未解决" : "已解决").severity(alarm.getSeverity())
                        .deviceId(alarm.getDeviceId()).build());
            });
        }
        return deviceLogDtoList;
    }

    /**
     * 事件日志
     *
     * @param deviceLogParam
     * @return
     */
    private Pager<DeviceLogDto> getEventLog(DeviceLogParam deviceLogParam) {
        List<DeviceLogDto> deviceLogDtoList = new ArrayList<>();

        QEventTriggerRecord query = new QEventTriggerRecord();

        Long tenantId = LoginContextHolder.getContext().getUser().getTenantId();
        if (null != tenantId) {
            query.tenantId.eq(tenantId);
        }
        if (ToolUtil.isNotEmpty(deviceLogParam.getDeviceId())) {
            query.deviceId.eq(deviceLogParam.getDeviceId());
        }
        if (ToolUtil.isNotEmpty(deviceLogParam.getContent())) {
            query.eventName.eq(deviceLogParam.getContent());
        }
        if (null != deviceLogParam.getTimeFrom()) {
            query.createTime.ge(LocalDateTimeUtils.getLDTByMilliSeconds(deviceLogParam.getTimeFrom() * 1000));
        }
        if (null != deviceLogParam.getTimeTill()) {
            query.createTime.lt(LocalDateTimeUtils.getLDTByMilliSeconds(deviceLogParam.getTimeTill() * 1000));
        }
        query.orderBy().createTime.desc();

        PagedList<EventTriggerRecord> pagedList = query.setFirstRow((deviceLogParam.getPage() - 1) * deviceLogParam.getMaxRow())
                .setMaxRows(deviceLogParam.getMaxRow()).findPagedList();

        if (ToolUtil.isNotEmpty(pagedList.getList())) {
            pagedList.getList().forEach(service -> {
                deviceLogDtoList.add(DeviceLogDto.builder().logType(LOG_TYPE_EVENT).content(service.getEventName())
                        .triggerTime(LocalDateTimeUtils.formatTime(service.getCreateTime())).deviceId(service.getDeviceId())
                        .key(service.getKey())
                        .param(service.getEventValue()).build());
            });
        }
        return new Pager<>(deviceLogDtoList, pagedList.getTotalCount());
    }

    /**
     * 服务日志
     *
     * @param deviceLogParam
     * @return
     */
    private Pager<DeviceLogDto> getServiceLog(DeviceLogParam deviceLogParam) {
        List<DeviceLogDto> deviceLogDtoList = new ArrayList<>();

        QServiceExecuteRecord query = new QServiceExecuteRecord();

        Long tenantId = LoginContextHolder.getContext().getUser().getTenantId();
        if (null != tenantId) {
            query.tenantId.eq(tenantId);
        }
        if (ToolUtil.isNotEmpty(deviceLogParam.getDeviceId())) {
            query.deviceId.eq(deviceLogParam.getDeviceId());
        }
        if (ToolUtil.isNotEmpty(deviceLogParam.getContent())) {
            query.serviceName.eq(deviceLogParam.getContent());
        }
        if (ToolUtil.isNotEmpty(deviceLogParam.getTriggerType())) {
            query.executeType.eq(deviceLogParam.getTriggerType());
        }
        if (ToolUtil.isNotEmpty(deviceLogParam.getTriggerUser())) {
            query.executeUser.eq(deviceLogParam.getTriggerUser());
        }
        if (ToolUtil.isNotEmpty(deviceLogParam.getEventRuleId())) {
            query.executeRuleId.eq(deviceLogParam.getEventRuleId());
        }
        if (null != deviceLogParam.getTimeFrom()) {
            query.createTime.ge(LocalDateTimeUtils.getLDTByMilliSeconds(deviceLogParam.getTimeFrom() * 1000));
        }
        if (null != deviceLogParam.getTimeTill()) {
            query.createTime.lt(LocalDateTimeUtils.getLDTByMilliSeconds(deviceLogParam.getTimeTill() * 1000));
        }
        query.orderBy().createTime.desc();

        PagedList<ServiceExecuteRecord> pagedList = query.setFirstRow((deviceLogParam.getPage() - 1) * deviceLogParam.getMaxRow())
                .setMaxRows(deviceLogParam.getMaxRow()).findPagedList();

        if (ToolUtil.isNotEmpty(pagedList.getList())) {
            pagedList.getList().forEach(service -> {

                String triggerBody = null != service.getExecuteUser() ?
                        DefinitionsUtil.getSysUserName(service.getExecuteUser()) : DefinitionsUtil.getTriggerName(service.getExecuteRuleId());

                deviceLogDtoList.add(DeviceLogDto.builder().logType(LOG_TYPE_SERVICE).content(service.getServiceName()).eventRuleId(service.getExecuteRuleId())
                        .triggerTime(LocalDateTimeUtils.formatTime(service.getCreateTime())).deviceId(service.getDeviceId()).userId(service.getExecuteUser())
                        .param(service.getParam()).triggerType(service.getExecuteType()).triggerBody(triggerBody).build());
            });
        }
        return new Pager<>(deviceLogDtoList, pagedList.getTotalCount());
    }

    /**
     * 场景日志
     *
     * @param deviceLogParam
     * @return
     */
    private Pager<DeviceLogDto> getScenesLog(DeviceLogParam deviceLogParam) {
        List<DeviceLogDto> deviceLogDtoList = new ArrayList<>();

        QScenesTriggerRecord query = new QScenesTriggerRecord();
        Long tenantId = LoginContextHolder.getContext().getUser().getTenantId();
        if (null != tenantId) {
            query.tenantId.eq(tenantId);
        }
        if (null != deviceLogParam.getEventRuleId()) {
            query.ruleId.eq(deviceLogParam.getEventRuleId());
        }
        if (ToolUtil.isNotEmpty(deviceLogParam.getTriggerType())) {
            query.triggerType.eq(deviceLogParam.getTriggerType());
        }
        if (null != deviceLogParam.getTriggerUser()) {
            query.triggerUser.eq(deviceLogParam.getTriggerUser());
        }
        if (null != deviceLogParam.getTimeFrom()) {
            query.createTime.ge(LocalDateTimeUtils.getLDTByMilliSeconds(deviceLogParam.getTimeFrom() * 1000));
        }
        if (null != deviceLogParam.getTimeTill()) {
            query.createTime.lt(LocalDateTimeUtils.getLDTByMilliSeconds(deviceLogParam.getTimeTill() * 1000));
        }

        if (ToolUtil.isNotEmpty(deviceLogParam.getTriggerDeviceId())) {
            List<Long> triggerRuleIds = new QProductEventRelation().select(QProductEventRelation.alias().eventRuleId).relationId.eq(deviceLogParam.getTriggerDeviceId()).findSingleAttributeList();
            if (ToolUtil.isEmpty(triggerRuleIds)) {
                return new Pager<>(Collections.emptyList(), 0);
            }
            query.ruleId.in(triggerRuleIds);
        }

        if (ToolUtil.isNotEmpty(deviceLogParam.getDeviceId())) {
            List<Long> executeRuleIds = new QProductEventService().select(QProductEventService.alias().eventRuleId).executeDeviceId.eq(deviceLogParam.getDeviceId()).findSingleAttributeList();
            if (ToolUtil.isEmpty(executeRuleIds)) {
                return new Pager<>(Collections.emptyList(), 0);
            }
            query.ruleId.in(executeRuleIds);
        }

        query.orderBy().createTime.desc();
        PagedList<ScenesTriggerRecord> pagedList = query.setFirstRow((deviceLogParam.getPage() - 1) * deviceLogParam.getMaxRow())
                .setMaxRows(deviceLogParam.getMaxRow()).findPagedList();

        if (ToolUtil.isEmpty(pagedList.getList())) {
            return new Pager<>(deviceLogDtoList, pagedList.getTotalCount());
        }
        //关联触发设备
        List<Long> eventRuleIds = pagedList.getList().parallelStream().map(ScenesTriggerRecord::getRuleId).collect(Collectors.toList());
        String sql = "select distinct d.device_id,d.name,p.event_rule_id from product_event_relation p LEFT JOIN device d on d.device_id = p.relation_id where p.event_rule_id in (:eventRuleIds)";

        List<DeviceRelationDto> triggerDeviceDtos = DB.findDto(DeviceRelationDto.class, sql).setParameter("eventRuleIds", eventRuleIds).findList();
        Map<Long, List<DeviceRelationDto>> triggerDeviceMap = triggerDeviceDtos.parallelStream()
                .collect(Collectors.groupingBy(DeviceRelationDto::getEventRuleId));

        //关联 执行设备
        sql = "select distinct d.device_id,d.name,p.event_rule_id from product_event_service p LEFT JOIN device d on d.device_id = p.execute_device_id where p.event_rule_id in (:eventRuleIds)";
        List<DeviceRelationDto> executeDeviceDtos = DB.findDto(DeviceRelationDto.class, sql).setParameter("eventRuleIds", eventRuleIds).findList();
        Map<Long, List<DeviceRelationDto>> executeDeviceMap = executeDeviceDtos.parallelStream()
                .collect(Collectors.groupingBy(DeviceRelationDto::getEventRuleId));

        pagedList.getList().forEach(service -> {
            deviceLogDtoList.add(DeviceLogDto.builder().logType(LOG_TYPE_SCENES).content(service.getRuleName()).eventRuleId(service.getRuleId())
                    .triggerTime(LocalDateTimeUtils.formatTime(service.getCreateTime())).triggerType(service.getTriggerType())
                    .triggerBody(Optional.ofNullable(service.getTriggerUser()).map(DefinitionsUtil::getSysUserName).orElse("-"))
                    .triggerDevice(triggerDeviceMap.get(service.getRuleId())).executeDevice(executeDeviceMap.get(service.getRuleId()))
                    .build());
        });
        return new Pager<>(deviceLogDtoList, pagedList.getTotalCount());
    }

    /**
     * 记录场景日志
     */
    public void recordSceneLog(Long eventRuleId, String type, Long userId) {
        Map<String, Object> serviceLogInfo = new ConcurrentHashMap<>(3);
        serviceLogInfo.put("eventRuleId", eventRuleId);
        serviceLogInfo.put("triggerType", "自动".equals(type) ? "场景联动" : type);
        if (null != userId) {
            serviceLogInfo.put("triggerUser", userId);
        }

        WorkerWrapper<Map<String, Object>, Boolean> deviceServiceLogWork = new WorkerWrapper.Builder<Map<String, Object>, Boolean>().id("deviceServiceLogWorker")
                .worker(deviceServiceLogWorker).param(serviceLogInfo).callback(ICallback.PRINT_EXCEPTION_STACK_TRACE)
                .build();

        Map<String, Object> scenesLogInfo = new ConcurrentHashMap<>(3);
        scenesLogInfo.put("eventRuleId", eventRuleId);
        scenesLogInfo.put("triggerType", type);
        if (null != userId) {
            scenesLogInfo.put("triggerUser", userId);
        }
        WorkerWrapper<Map<String, Object>, Boolean> scenesLogWork = new WorkerWrapper.Builder<Map<String, Object>, Boolean>().id("scenesLogWorker")
                .worker(scenesLogWorker).param(scenesLogInfo).callback(ICallback.PRINT_EXCEPTION_STACK_TRACE)
                .build();

        try {
            Async.beginWork(1000, deviceServiceLogWork, scenesLogWork);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
