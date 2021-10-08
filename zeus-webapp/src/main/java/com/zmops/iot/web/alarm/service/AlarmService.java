package com.zmops.iot.web.alarm.service;

import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.alarm.AlarmMessage;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.ProductEvent;
import com.zmops.iot.domain.product.query.QProductEvent;
import com.zmops.iot.media.AlarmCallback;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.LocalDateTimeUtils;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.alarm.dto.AlarmDto;
import com.zmops.iot.web.alarm.dto.param.AlarmParam;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.iot.web.device.service.DeviceService;
import com.zmops.iot.web.product.dto.ProductEventRuleDto;
import com.zmops.zeus.driver.entity.ZbxProblemInfo;
import com.zmops.zeus.driver.service.ZbxProblem;
import io.ebean.DB;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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

    @Autowired
    DeviceService deviceService;


    public void alarm(Map<String, String> alarmInfo) {
        String deviceId    = alarmInfo.get("hostname");
        String eventRuleId = alarmInfo.get("triggerName");

        if (ToolUtil.isEmpty(deviceId) || ToolUtil.isEmpty(eventRuleId)) {
            return;
        }

        Device       device       = new QDevice().deviceId.eq(deviceId).findOne();
        ProductEvent productEvent = new QProductEvent().eventRuleId.eq(Long.parseLong(eventRuleId)).findOne();

        List<AlarmMessage> alarmMessages = new ArrayList<>();

        if (null != device && null != productEvent) {
            String alarmmessage = "设备:" + device.getName() + "发生告警，告警内容：" + productEvent.getEventRuleName();
            alarmMessages.add(AlarmMessage.builder().alarmMessage(alarmmessage).build());
            alarmCallbacks.forEach(alarmCallback -> {
//            if (alarmCallback.getType().equals("welink")) {
                alarmCallback.doAlarm(alarmMessages);
//            }
            });
        }
    }


    public Pager<AlarmDto> getAlarmByPage(AlarmParam alarmParam) {

        List<ZbxProblemInfo> zbxProblemInfos = getZbxAlarm(alarmParam);
        if (ToolUtil.isEmpty(zbxProblemInfos)) {
            return new Pager<>();
        }
        //分页
        List<ZbxProblemInfo> problemList = zbxProblemInfos.stream()
                .skip((alarmParam.getPage() - 1) * alarmParam.getMaxRow())
                .limit(alarmParam.getMaxRow()).collect(Collectors.toList());
        if (ToolUtil.isEmpty(problemList)) {
            return new Pager<>(Collections.emptyList(),zbxProblemInfos.size());
        }
        //根据triggerid查询出 所属设备
        List<String> triggerIds = problemList.parallelStream().map(ZbxProblemInfo::getObjectid).collect(Collectors.toList());
        List<DeviceDto> deviceList = DB.findDto(DeviceDto.class, "select name,r.zbx_id from device d INNER JOIN (select relation_id,zbx_id from product_event_relation where zbx_id in (:zbxIds)) r on r.relation_id=d.device_id")
                .setParameter("zbxIds", triggerIds).findList();
        Map<String, String> deviceMap = deviceList.parallelStream().collect(Collectors.toMap(DeviceDto::getZbxId, DeviceDto::getName));

        List<ProductEventRuleDto> ruleList = DB.findDto(ProductEventRuleDto.class, "select event_rule_name,r.zbx_id from product_event d INNER JOIN (select event_rule_id,zbx_id from product_event_relation where zbx_id in (:zbxIds)) r on r.event_rule_id=d.event_rule_id")
                .setParameter("zbxIds", triggerIds).findList();
        Map<String, String> ruleMap = ruleList.parallelStream().collect(Collectors.toMap(ProductEventRuleDto::getZbxId, ProductEventRuleDto::getEventRuleName));


        List<AlarmDto> alarmDtoList = new ArrayList<>();
        problemList.forEach(zbxProblemInfo -> {
            AlarmDto alarmDto = new AlarmDto();
            BeanUtils.copyProperties(zbxProblemInfo, alarmDto);
            alarmDto.setRClock(zbxProblemInfo.getR_clock());
            alarmDto.setClock(LocalDateTimeUtils.convertTimeToString(Integer.parseInt(zbxProblemInfo.getClock()), "yyyy-MM-dd HH:mm:ss"));
            alarmDto.setRClock("0".equals(zbxProblemInfo.getR_clock()) ? "0" :
                    LocalDateTimeUtils.convertTimeToString(Integer.parseInt(zbxProblemInfo.getR_clock()), "yyyy-MM-dd HH:mm:ss"));
            alarmDto.setStatus("0".equals(zbxProblemInfo.getR_clock()) ? "未解决" : "已解决");
            alarmDto.setAcknowledged("0".equals(zbxProblemInfo.getAcknowledged()) ? "未确认" : "已确认");
            if (null != deviceMap.get(zbxProblemInfo.getObjectid())) {
                alarmDto.setDeviceName(deviceMap.get(zbxProblemInfo.getObjectid()));
            }
            if (null != ruleMap.get(zbxProblemInfo.getObjectid())) {
                alarmDto.setName(ruleMap.get(zbxProblemInfo.getObjectid()));
            }
            alarmDtoList.add(alarmDto);
        });

        return new Pager<>(alarmDtoList,zbxProblemInfos.size());
    }

    public List<AlarmDto> getAlarmList(AlarmParam alarmParam) {
        String hostId = null;
        Assert.state(ToolUtil.isNotEmpty(alarmParam.getDeviceId()), "设备ID不能为空");
        List<ZbxProblemInfo> problemList = getZbxAlarm(alarmParam);
        if (ToolUtil.isEmpty(problemList)) {
            return Collections.emptyList();
        }
        List<String> triggerIds = problemList.parallelStream().map(ZbxProblemInfo::getObjectid).map(Objects::toString).collect(Collectors.toList());
        List<ProductEventRuleDto> ruleList = DB.findDto(ProductEventRuleDto.class, "select d.event_rule_name,r.zbx_id from product_event d INNER JOIN (select event_rule_id," +
                "zbx_id from product_event_relation where zbx_id in (:zbxIds)) r on r.event_rule_id=d.event_rule_id")
                .setParameter("zbxIds", triggerIds).findList();
        Map<String, String> ruleMap = ruleList.parallelStream().collect(Collectors.toMap(ProductEventRuleDto::getZbxId, ProductEventRuleDto::getEventRuleName));

        List<AlarmDto> alarmDtoList = new ArrayList<>();
        problemList.forEach(zbxProblemInfo -> {
            AlarmDto alarmDto = new AlarmDto();
            BeanUtils.copyProperties(zbxProblemInfo, alarmDto);
            alarmDto.setRClock(zbxProblemInfo.getR_clock());
            if (null != ruleMap.get(zbxProblemInfo.getObjectid())) {
                alarmDto.setName(ruleMap.get(zbxProblemInfo.getObjectid()));
            }
            alarmDtoList.add(alarmDto);
        });


        return alarmDtoList;
    }

    public List<ZbxProblemInfo> getZbxAlarm(AlarmParam alarmParam) {
        String       hostId = null;
        List<String> deviceIds;
        if (ToolUtil.isNotEmpty(alarmParam.getDeviceId())) {
            deviceIds = Collections.singletonList(alarmParam.getDeviceId());
        } else {
            deviceIds = deviceService.getDeviceIds();
        }

        List<String> zbxIds = new QDevice().select(QDevice.alias().zbxId).deviceId.in(deviceIds).findSingleAttributeList();
        if (ToolUtil.isEmpty(zbxIds)) {
            return Collections.EMPTY_LIST;
        }
        hostId = zbxIds.toString();
        //从zbx取告警记录
        String problem = zbxProblem.getProblem(hostId, alarmParam.getTimeFrom(), alarmParam.getTimeTill(), alarmParam.getRecent());
        return JSONObject.parseArray(problem, ZbxProblemInfo.class);
    }


    public List<AlarmDto> getEventList(AlarmParam alarmParam) {
        String hostId = null;
        Assert.state(ToolUtil.isNotEmpty(alarmParam.getDeviceId()), "设备ID不能为空");
        List<ZbxProblemInfo> problemList = getZbxAlarm(alarmParam);
        if (ToolUtil.isEmpty(problemList)) {
            return Collections.emptyList();
        }
        List<String> triggerIds = problemList.parallelStream().map(ZbxProblemInfo::getObjectid).map(Objects::toString).collect(Collectors.toList());
        List<ProductEventRuleDto> ruleList = DB.findDto(ProductEventRuleDto.class, "select event_rule_name,r.zbx_id from product_event d INNER JOIN (select event_rule_id," +
                "zbx_id from product_event_relation where zbx_id in (:zbxIds)) r on r.event_rule_id=d.event_rule_id")
                .setParameter("zbxIds", triggerIds).findList();
        Map<String, String> ruleMap = ruleList.parallelStream().collect(Collectors.toMap(ProductEventRuleDto::getZbxId, ProductEventRuleDto::getEventRuleName));

        List<AlarmDto> alarmDtoList = new ArrayList<>();
        problemList.forEach(zbxProblemInfo -> {
            AlarmDto alarmDto = new AlarmDto();
            BeanUtils.copyProperties(zbxProblemInfo, alarmDto);
            alarmDto.setRClock(zbxProblemInfo.getR_clock());
            if (null != ruleMap.get(zbxProblemInfo.getObjectid())) {
                alarmDto.setName(ruleMap.get(zbxProblemInfo.getObjectid()));
            }
            alarmDtoList.add(alarmDto);
        });


        return alarmDtoList;
    }

    public List<ZbxProblemInfo> getEventProblem(AlarmParam alarmParam) {
        String hostId = null;
        List<String> deviceIds;
        if (ToolUtil.isNotEmpty(alarmParam.getDeviceId())) {
            deviceIds = Collections.singletonList(alarmParam.getDeviceId());
        } else {
            deviceIds = deviceService.getDeviceIds();
        }

        List<String> zbxIds = new QDevice().select(QDevice.alias().zbxId).deviceId.in(deviceIds).findSingleAttributeList();
        if (ToolUtil.isEmpty(zbxIds)) {
            return Collections.EMPTY_LIST;
        }
        hostId = zbxIds.toString();
        //从zbx取告警记录
        String problem = zbxProblem.getEventProblem(hostId, alarmParam.getTimeFrom(), alarmParam.getTimeTill(), alarmParam.getRecent());
        return JSONObject.parseArray(problem, ZbxProblemInfo.class);
    }

    public void acknowledgement(String eventId) {
        zbxProblem.acknowledgement(eventId, 2);
    }

    public void resolve(String eventId) {
        zbxProblem.acknowledgement(eventId, 1);
    }
}
