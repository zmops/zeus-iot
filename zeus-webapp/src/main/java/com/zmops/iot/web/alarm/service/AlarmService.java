package com.zmops.iot.web.alarm.service;

import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.alarm.AlarmMessage;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.mediaType.AlarmCallback;
import com.zmops.iot.web.alarm.dto.AlarmDto;
import com.zmops.iot.web.alarm.dto.param.AlarmParam;
import com.zmops.zeus.driver.entity.ZbxProblemInfo;
import com.zmops.zeus.driver.service.ZbxProblem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

        //分页
        List<ZbxProblemInfo> problemList = zbxProblemInfos.stream().skip((alarmParam.getPage() - 1) * alarmParam.getMaxRow()).limit(alarmParam.getMaxRow()).collect(Collectors.toList());

        //根据triggerid查询出deviceId


        return null;
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
        String               problem         = zbxProblem.getProblem(hostId, alarmParam.getTimeFrom(),alarmParam.getTimeTill(),alarmParam.getRecent());
        List<ZbxProblemInfo> zbxProblemInfos = JSONObject.parseArray(problem, ZbxProblemInfo.class);


        return zbxProblemInfos;
    }

}
