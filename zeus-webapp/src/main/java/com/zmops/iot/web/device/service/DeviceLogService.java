package com.zmops.iot.web.device.service;

import com.zmops.iot.domain.device.ServiceExecuteRecord;
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

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yefei
 **/
@Service
public class DeviceLogService {

    private static final String LOG_TYPE_ALARM = "告警日志";
    private static final String LOG_TYPE_EVENT = "事件日志";
    private static final String LOG_TYPE_SERVICE = "服务日志";

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
                            .triggerTime(LocalDateTimeUtils.convertDateToLocalDateTime(Integer.parseInt(alarm.getClock())))
                            .status("0".equals(alarm.getRClock()) ? "已解决" : "未解决").build());
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
                            .triggerTime(LocalDateTimeUtils.convertDateToLocalDateTime(Integer.parseInt(alarm.getClock())))
                            .status("0".equals(alarm.getRClock()) ? "已解决" : "未解决").build());
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
                            .triggerTime(service.getCreateTime())
                            .param(service.getParam()).build());
                });
            }
        }
        deviceLogDtoList.parallelStream()
                .sorted((o1, o2) -> (int) LocalDateTimeUtils.betweenTwoTime(o1.getTriggerTime(), o2.getTriggerTime(), ChronoUnit.SECONDS)).collect(Collectors.toList());
        return deviceLogDtoList;
    }


    public Pager<DeviceLogDto> getLogByPage(String deviceId, String logType, Long timeFrom, Long timeTill, int page, int maxSize) {
        List<DeviceLogDto> deviceLogDtoList = new ArrayList<>();
        if (ToolUtil.isNotEmpty(logType) && LOG_TYPE_ALARM.equals(logType)) {
            AlarmParam alarmParam = new AlarmParam();
            alarmParam.setDeviceId(deviceId);
            alarmParam.setTimeFrom(timeFrom);
            alarmParam.setTimeTill(timeTill);
            List<AlarmDto> alarmList = alarmService.getAlarmList(alarmParam);
            if (ToolUtil.isNotEmpty(alarmList)) {
                alarmList.forEach(alarm -> {
                    deviceLogDtoList.add(DeviceLogDto.builder().logType(LOG_TYPE_ALARM).content(alarm.getName())
                            .triggerTime(LocalDateTimeUtils.convertDateToLocalDateTime(Integer.parseInt(alarm.getClock())))
                            .status("0".equals(alarm.getRClock()) ? "已解决" : "未解决").build());
                });
            }
        }

        if (ToolUtil.isNotEmpty(logType) && LOG_TYPE_SERVICE.equals(logType)) {
            QServiceExecuteRecord query = new QServiceExecuteRecord().deviceId.eq(deviceId);
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
                            .triggerTime(service.getCreateTime())
                            .param(service.getParam()).build());
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
                            .triggerTime(LocalDateTimeUtils.convertDateToLocalDateTime(Integer.parseInt(alarm.getClock())))
                            .status("0".equals(alarm.getRClock()) ? "已解决" : "未解决").build());
                });
            }
        }

        List<DeviceLogDto> res = deviceLogDtoList.parallelStream().skip((page - 1) * maxSize)
                .limit(maxSize).collect(Collectors.toList());
        return new Pager<>(res, deviceLogDtoList.size());
    }
}
