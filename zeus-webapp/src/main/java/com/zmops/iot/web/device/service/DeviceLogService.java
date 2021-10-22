package com.zmops.iot.web.device.service;

import com.zmops.iot.domain.device.ScenesTriggerRecord;
import com.zmops.iot.domain.device.ServiceExecuteRecord;
import com.zmops.iot.domain.device.query.QScenesTriggerRecord;
import com.zmops.iot.domain.device.query.QServiceExecuteRecord;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.LocalDateTimeUtils;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.alarm.dto.AlarmDto;
import com.zmops.iot.web.alarm.dto.param.AlarmParam;
import com.zmops.iot.web.alarm.service.AlarmService;
import com.zmops.iot.web.device.dto.DeviceLogDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yefei
 **/
@Service
public class DeviceLogService {

    private static final String LOG_TYPE_ALARM   = "告警日志";
    private static final String LOG_TYPE_EVENT   = "事件日志";
    private static final String LOG_TYPE_SERVICE = "服务日志";
    private static final String LOG_TYPE_SCENES  = "场景日志";

    @Autowired
    AlarmService alarmService;

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
                            .triggerTime(alarm.getClock())
                            .status("0".equals(alarm.getRClock()) ? "未解决" : "已解决").build());
                });
            }
        }

        if (ToolUtil.isEmpty(logType) || (ToolUtil.isNotEmpty(logType) && LOG_TYPE_EVENT.equals(logType))) {
            AlarmParam alarmParam = new AlarmParam();
            alarmParam.setDeviceId(deviceId);
            alarmParam.setTimeFrom(timeFrom);
            alarmParam.setTimeTill(timeTill);
            List<AlarmDto> alarmList = alarmService.getEventList(alarmParam);
            if (ToolUtil.isNotEmpty(alarmList)) {
                alarmList.forEach(alarm -> {
                    deviceLogDtoList.add(DeviceLogDto.builder().logType(LOG_TYPE_ALARM).content(alarm.getName())
                            .triggerTime(LocalDateTimeUtils.convertTimeToString(Integer.parseInt(alarm.getClock()), "yyyy-MM-dd HH:ss:mm"))
                            .status("0".equals(alarm.getRClock()) ? "未解决" : "已解决").build());
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


    public Pager<DeviceLogDto> getLogByPage(String deviceId, String logType, Long timeFrom, Long timeTill, String content, int page, int maxSize) {
        List<DeviceLogDto> deviceLogDtoList;
        if (ToolUtil.isNotEmpty(logType) && LOG_TYPE_ALARM.equals(logType)) {

            deviceLogDtoList = getAlarmLog(deviceId, timeFrom, timeTill, content);

        } else if (ToolUtil.isNotEmpty(logType) && LOG_TYPE_SERVICE.equals(logType)) {

            deviceLogDtoList = getServiceLog(deviceId, timeFrom, timeTill, content);

        } else if (ToolUtil.isNotEmpty(logType) && LOG_TYPE_SCENES.equals(logType)) {

            deviceLogDtoList = getScenesLog(deviceId, timeFrom, timeTill, content);

        } else {

            deviceLogDtoList = getEventLog(deviceId, timeFrom, timeTill, content);

        }

        List<DeviceLogDto> res = deviceLogDtoList.parallelStream().skip((page - 1) * maxSize)
                .limit(maxSize).collect(Collectors.toList());
        return new Pager<>(res, deviceLogDtoList.size());
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
                        .triggerTime(alarm.getClock())
                        .status("0".equals(alarm.getRClock()) ? "未解决" : "已解决").severity(alarm.getSeverity())
                        .deviceId(alarm.getDeviceId()).deviceName(alarm.getDeviceName()).build());
            });
        }
        return deviceLogDtoList;
    }

    /**
     * 事件日志
     *
     * @param deviceId
     * @param timeFrom
     * @param timeTill
     * @param content
     * @return
     */
    private List<DeviceLogDto> getEventLog(String deviceId, Long timeFrom, Long timeTill, String content) {
        List<DeviceLogDto> deviceLogDtoList = new ArrayList<>();
        AlarmParam alarmParam = new AlarmParam();
        if (ToolUtil.isNotEmpty(deviceId)) {
            alarmParam.setDeviceId(deviceId);
        }
        alarmParam.setTimeFrom(timeFrom);
        alarmParam.setTimeTill(timeTill);
        List<AlarmDto> alarmList = alarmService.getEventList(alarmParam);
        if (ToolUtil.isNotEmpty(alarmList)) {
            alarmList.forEach(alarm -> {
                deviceLogDtoList.add(DeviceLogDto.builder().logType(LOG_TYPE_ALARM).content(alarm.getName())
                        .triggerTime(LocalDateTimeUtils.convertTimeToString(Integer.parseInt(alarm.getClock()), "yyyy-MM-dd HH:ss:mm"))
                        .status("0".equals(alarm.getRClock()) ? "未解决" : "已解决").build());
            });
        }
        return deviceLogDtoList;
    }

    /**
     * 服务日志
     *
     * @param deviceId
     * @param timeFrom
     * @param timeTill
     * @param content
     * @return
     */
    private List<DeviceLogDto> getServiceLog(String deviceId, Long timeFrom, Long timeTill, String content) {
        List<DeviceLogDto> deviceLogDtoList = new ArrayList<>();
        QServiceExecuteRecord query = new QServiceExecuteRecord();
        if (ToolUtil.isNotEmpty(deviceId)) {
            query.deviceId.eq(deviceId);
        }
        if (ToolUtil.isNotEmpty(content)) {
            query.serviceName.contains(content);
        }
        if (null != timeFrom) {
            query.createTime.ge(LocalDateTimeUtils.getLDTByMilliSeconds(timeFrom * 1000));
        }
        if (null != timeTill) {
            query.createTime.lt(LocalDateTimeUtils.getLDTByMilliSeconds(timeTill * 1000));
        }
        query.orderBy().createTime.desc();
        List<ServiceExecuteRecord> list = query.findList();
        if (ToolUtil.isNotEmpty(list)) {
            list.forEach(service -> {
                deviceLogDtoList.add(DeviceLogDto.builder().logType(LOG_TYPE_SERVICE).content(service.getServiceName())
                        .triggerTime(LocalDateTimeUtils.formatTime(service.getCreateTime()))
                        .param(service.getParam()).build());
            });
        }
        return deviceLogDtoList;
    }

    /**
     * 场景日志
     *
     * @param deviceId
     * @param timeFrom
     * @param timeTill
     * @param content
     * @return
     */
    private List<DeviceLogDto> getScenesLog(String deviceId, Long timeFrom, Long timeTill, String content) {
        List<DeviceLogDto> deviceLogDtoList = new ArrayList<>();
        QScenesTriggerRecord query = new QScenesTriggerRecord();
        if (ToolUtil.isNotEmpty(content)) {
            query.ruleName.contains(content);
        }
        if (null != timeFrom) {
            query.createTime.ge(LocalDateTimeUtils.getLDTByMilliSeconds(timeFrom * 1000));
        }
        if (null != timeTill) {
            query.createTime.lt(LocalDateTimeUtils.getLDTByMilliSeconds(timeTill * 1000));
        }
        query.orderBy().createTime.desc();
        List<ScenesTriggerRecord> list = query.findList();
        if (ToolUtil.isNotEmpty(list)) {
            list.forEach(service -> {
                deviceLogDtoList.add(DeviceLogDto.builder().logType(LOG_TYPE_SCENES).content(service.getRuleName())
                        .triggerTime(LocalDateTimeUtils.formatTime(service.getCreateTime()))
                        .build());
            });
        }
        return deviceLogDtoList;
    }
}
