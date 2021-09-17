package com.zmops.iot.web.alarm.service;

import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.alarm.AlarmMessage;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.mediaType.AlarmCallback;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.alarm.dto.AlarmDto;
import com.zmops.iot.web.alarm.dto.param.AlarmParam;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.zeus.driver.entity.ZbxProblemInfo;
import com.zmops.zeus.driver.service.ZbxProblem;
import io.ebean.DB;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yefei
 **/
@Service
public class AlarmService {

    @Autowired
    Collection<AlarmCallback> alarmCallbacks;

    @Autowired
    ZbxProblem zbxProblem;


    public void test() {
        List<AlarmMessage> alarmMessages = new ArrayList<>();
        alarmMessages.add(AlarmMessage.builder().alarmMessage("DELTA 一级预警").build());
        alarmCallbacks.forEach(alarmCallback -> {
            if (alarmCallback.getType().equals("welink")) {
                alarmCallback.doAlarm(alarmMessages);
            }
        });
    }


    public List<AlarmDto> getAlarmByPage(AlarmParam alarmParam) {

        List<ZbxProblemInfo> zbxProblemInfos = getAlarmList(alarmParam);
        if (ToolUtil.isEmpty(zbxProblemInfos)) {
            return Collections.emptyList();
        }
        //分页
        List<ZbxProblemInfo> problemList = zbxProblemInfos.stream().skip((alarmParam.getPage() - 1) * alarmParam.getMaxRow()).limit(alarmParam.getMaxRow()).collect(Collectors.toList());

        //根据triggerid查询出 所属设备
        List<String> triggerIds = problemList.parallelStream().map(ZbxProblemInfo::getObjectid).collect(Collectors.toList());
        List<DeviceDto> deviceList = DB.findDto(DeviceDto.class, "select name,r.zbx_id from device d INNER JOIN (select relation_id,zbx_id from product_event_relation where zbx_id in (:zbxIds)) r on r.relation_id=d.device_id")
                .setParameter("zbxIds", triggerIds).findList();
        Map<String, String> deviceMap = deviceList.parallelStream().collect(Collectors.toMap(DeviceDto::getZbxId, DeviceDto::getName));

        List<AlarmDto> alarmDtoList = new ArrayList<>();
        problemList.forEach(zbxProblemInfo -> {
            AlarmDto alarmDto = new AlarmDto();
            BeanUtils.copyProperties(zbxProblemInfo, alarmDto);
            alarmDto.setRClock(zbxProblemInfo.getR_clock());
            if (null != deviceMap.get(zbxProblemInfo.getObjectid())) {
                alarmDto.setDeviceName(deviceMap.get(zbxProblemInfo.getObjectid()));
            }
            alarmDtoList.add(alarmDto);
        });

        return alarmDtoList;
    }

    public List<ZbxProblemInfo> getAlarmList(AlarmParam alarmParam) {
        String hostId = null;
        if (null != alarmParam.getDeviceId()) {
            Device one = new QDevice().deviceId.eq(alarmParam.getDeviceId()).findOne();
            if (null == one) {
                return Collections.EMPTY_LIST;
            }
            hostId = one.getZbxId();
        }
        //从zbx取告警记录
        String               problem         = zbxProblem.getProblem(hostId, alarmParam.getTimeFrom(), alarmParam.getTimeTill(), alarmParam.getRecent());
        List<ZbxProblemInfo> zbxProblemInfos = JSONObject.parseArray(problem, ZbxProblemInfo.class);


        return zbxProblemInfos;
    }

}
