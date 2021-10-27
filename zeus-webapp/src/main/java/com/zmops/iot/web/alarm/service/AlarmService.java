package com.zmops.iot.web.alarm.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
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
import io.ebean.DtoQuery;
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

    @Autowired
    DeviceService deviceService;


    public void alarm(Map<String, Object> alarmInfo) {
        List<String> deviceIds = (List) alarmInfo.get("hostname");
        String eventRuleId = (String) alarmInfo.get("triggerName");

        if (ToolUtil.isEmpty(deviceIds) || ToolUtil.isEmpty(eventRuleId)) {
            return;
        }

        List<Device> deviceList = new QDevice().deviceId.in(deviceIds).findList();
        ProductEvent productEvent = new QProductEvent().eventRuleId.eq(Long.parseLong(eventRuleId)).findOne();

        List<AlarmMessage> alarmMessages = new ArrayList<>();

        if (ToolUtil.isNotEmpty(deviceList) && null != productEvent) {
            String deviceName = deviceList.parallelStream().map(Device::getName).collect(Collectors.joining(","));
            String alarmmessage = "设备:" + deviceName + "发生告警，告警内容：" + productEvent.getEventRuleName();
            alarmMessages.add(AlarmMessage.builder().alarmMessage(alarmmessage).build());
            alarmCallbacks.forEach(alarmCallback -> {
                alarmCallback.doAlarm(alarmMessages);
            });
        }
    }

    public void action(Map<String, Object> alarmInfo) {
        List<String> deviceIds = (List) alarmInfo.get("hostname");
        String serviceName = (String) alarmInfo.get("serviceName");

        if (ToolUtil.isEmpty(deviceIds) || ToolUtil.isEmpty(serviceName)) {
            return;
        }

        List<Device> deviceList = new QDevice().deviceId.in(deviceIds).findList();
        List<String> deviceName = deviceList.parallelStream().map(Device::getName).collect(Collectors.toList());

        List<AlarmMessage> alarmMessages = new ArrayList<>();

        if (ToolUtil.isNotEmpty(deviceList)) {
            String alarmmessage = "设备:" + Joiner.on(",").join(deviceName) + "触发联动服务，服务名称：" + serviceName;
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
        if (ToolUtil.isNotEmpty(alarmParam.getStatusName()) && "已解决".equals(alarmParam.getStatusName())) {
            zbxProblemInfos = zbxProblemInfos.parallelStream().filter(o -> !"0".equals(o.getR_clock())).collect(Collectors.toList());
        }

        if (ToolUtil.isNotEmpty(alarmParam.getStatusName()) && "未解决".equals(alarmParam.getStatusName())) {
            zbxProblemInfos = zbxProblemInfos.parallelStream().filter(o -> "0".equals(o.getR_clock())).collect(Collectors.toList());
        }
        //分页
        List<ZbxProblemInfo> problemList = zbxProblemInfos.stream()
                .skip((alarmParam.getPage() - 1) * alarmParam.getMaxRow())
                .limit(alarmParam.getMaxRow()).collect(Collectors.toList());
        if (ToolUtil.isEmpty(problemList)) {
            return new Pager<>(Collections.emptyList(), zbxProblemInfos.size());
        }
        List<AlarmDto> alarmDtoList = formatAlarmList(problemList, alarmParam);

        return new Pager<>(alarmDtoList, zbxProblemInfos.size());
    }

    public List<AlarmDto> getAlarmList(AlarmParam alarmParam) {
        List<ZbxProblemInfo> problemList = getZbxAlarm(alarmParam);
        if (ToolUtil.isEmpty(problemList)) {
            return Collections.emptyList();
        }

        return formatAlarmList(problemList, alarmParam);
    }

    private List<AlarmDto> formatAlarmList(List<ZbxProblemInfo> problemList, AlarmParam alarmParam) {
        List<String> triggerIds = problemList.parallelStream().map(ZbxProblemInfo::getObjectid).map(Objects::toString).collect(Collectors.toList());

        //根据triggerid查询出 规则名称
        String sql = "select d.event_rule_name,r.zbx_id from product_event d INNER JOIN (select event_rule_id," +
                "zbx_id from product_event_relation where zbx_id in (:zbxIds)) r on r.event_rule_id=d.event_rule_id";
        if (ToolUtil.isNotEmpty(alarmParam.getName())) {
            sql += " where d.event_rule_name like :eventRuleName";
        }
        DtoQuery<ProductEventRuleDto> dtoQuery = DB.findDto(ProductEventRuleDto.class, sql).setParameter("zbxIds", triggerIds);
        if (ToolUtil.isNotEmpty(alarmParam.getName())) {
            dtoQuery.setParameter("eventRuleName", "%" + alarmParam.getName() + "%");
        }
        List<ProductEventRuleDto> ruleList = dtoQuery.findList();
        Map<String, String> ruleMap = ruleList.parallelStream().collect(Collectors.toMap(ProductEventRuleDto::getZbxId, ProductEventRuleDto::getEventRuleName));

        //根据triggerid查询出 所属设备
        sql = "select distinct name,device_id,r.zbx_id from device d INNER JOIN (select relation_id,zbx_id from product_event_relation where zbx_id in (:zbxIds)) r on r.relation_id=d.device_id";
        if (ToolUtil.isNotEmpty(alarmParam.getDeviceId())) {
            sql += " where d.device_id = :deviceId";
        }
        DtoQuery<DeviceDto> deviceQuery = DB.findDto(DeviceDto.class, sql).setParameter("zbxIds", triggerIds);
        if (ToolUtil.isNotEmpty(alarmParam.getDeviceId())) {
            deviceQuery.setParameter("deviceId", alarmParam.getDeviceId());
        }
        List<DeviceDto> deviceList = deviceQuery.findList();
        Map<String, List<DeviceDto>> deviceMap = deviceList.parallelStream().collect(Collectors.groupingBy(DeviceDto::getZbxId));

        List<AlarmDto> alarmDtoList = new ArrayList<>();
        problemList.forEach(zbxProblemInfo -> {
            if (null == ruleMap.get(zbxProblemInfo.getObjectid())) {
                return;
            }
            if (ToolUtil.isNotEmpty(deviceMap.get(zbxProblemInfo.getObjectid()))) {
                deviceMap.get(zbxProblemInfo.getObjectid()).forEach(deviceDto -> {
                    AlarmDto alarmDto = new AlarmDto();
                    BeanUtils.copyProperties(zbxProblemInfo, alarmDto);
                    alarmDto.setRClock(zbxProblemInfo.getR_clock());
                    alarmDto.setClock(LocalDateTimeUtils.convertTimeToString(Integer.parseInt(zbxProblemInfo.getClock()), "yyyy-MM-dd HH:mm:ss"));
                    alarmDto.setRClock("0".equals(zbxProblemInfo.getR_clock()) ? "0" :
                            LocalDateTimeUtils.convertTimeToString(Integer.parseInt(zbxProblemInfo.getR_clock()), "yyyy-MM-dd HH:mm:ss"));
                    alarmDto.setStatus("0".equals(zbxProblemInfo.getR_clock()) ? "未解决" : "已解决");
                    alarmDto.setAcknowledged("0".equals(zbxProblemInfo.getAcknowledged()) ? "未确认" : "已确认");

                    alarmDto.setDeviceName(deviceDto.getName());
                    alarmDto.setDeviceId(deviceDto.getDeviceId());

                    alarmDto.setName(ruleMap.get(zbxProblemInfo.getObjectid()));
                    alarmDtoList.add(alarmDto);
                });
            }
        });

        return alarmDtoList;
    }

    public List<ZbxProblemInfo> getZbxAlarm(AlarmParam alarmParam) {
        String hostId;
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
        String problem = zbxProblem.getProblem(hostId, alarmParam.getTimeFrom(), alarmParam.getTimeTill(), alarmParam.getRecent(), alarmParam.getSeverity());
        return JSONObject.parseArray(problem, ZbxProblemInfo.class);
    }


    public List<AlarmDto> getEventList(AlarmParam alarmParam) {
//        Assert.state(ToolUtil.isNotEmpty(alarmParam.getDeviceId()), "设备ID不能为空");
        List<ZbxProblemInfo> problemList = getEventProblem(alarmParam);
        if (ToolUtil.isEmpty(problemList)) {
            return Collections.emptyList();
        }
        List<String> triggerIds = problemList.parallelStream().map(ZbxProblemInfo::getObjectid).map(Objects::toString).collect(Collectors.toList());
        String sql = "select d.event_rule_name,r.zbx_id from product_event d INNER JOIN (select event_rule_id," +
                "zbx_id from product_event_relation where zbx_id in (:zbxIds)) r on r.event_rule_id=d.event_rule_id";
        if (ToolUtil.isNotEmpty(alarmParam.getName())) {
            sql += " where d.event_rule_name like :eventRuleName";
        }
        DtoQuery<ProductEventRuleDto> dtoQuery = DB.findDto(ProductEventRuleDto.class, sql)
                .setParameter("zbxIds", triggerIds);
        if (ToolUtil.isNotEmpty(alarmParam.getName())) {
            dtoQuery.setParameter("eventRuleName", "%" + alarmParam.getName() + "%");
        }
        List<ProductEventRuleDto> ruleList = dtoQuery.findList();
        Map<String, String> ruleMap = ruleList.parallelStream().collect(Collectors.toMap(ProductEventRuleDto::getZbxId, ProductEventRuleDto::getEventRuleName));

        List<AlarmDto> alarmDtoList = new ArrayList<>();
        problemList.forEach(zbxProblemInfo -> {
            if (null == ruleMap.get(zbxProblemInfo.getObjectid())) {
                return;
            }
            AlarmDto alarmDto = new AlarmDto();
            BeanUtils.copyProperties(zbxProblemInfo, alarmDto);
            alarmDto.setRClock(zbxProblemInfo.getR_clock());
            alarmDto.setName(ruleMap.get(zbxProblemInfo.getObjectid()));
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
