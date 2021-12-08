package com.zmops.iot.web.alarm.service;

import com.zmops.iot.domain.alarm.AlarmMessage;
import com.zmops.iot.domain.alarm.Problem;
import com.zmops.iot.domain.alarm.query.QProblem;
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
import com.zmops.iot.web.device.service.DeviceService;
import com.zmops.zeus.driver.service.ZbxProblem;
import io.ebean.PagedList;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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

    private static final String PROBLEM_RESOLVE = "已解決";


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
                alarmCallback.doAlarm(alarmMessages, deviceList.get(0).getTenantId());
            });
        }
    }

    public void action(Map<String, String> alarmInfo) {
        String deviceId = alarmInfo.get("hostname");
        String serviceName = alarmInfo.get("serviceName");

        if (ToolUtil.isEmpty(deviceId) || ToolUtil.isEmpty(serviceName)) {
            return;
        }

        Device device = new QDevice().deviceId.eq(deviceId).findOne();

        List<AlarmMessage> alarmMessages = new ArrayList<>();

        if (null != device) {
            String alarmmessage = "设备:" + device.getName() + "触发联动服务，服务名称：" + serviceName;
            alarmMessages.add(AlarmMessage.builder().alarmMessage(alarmmessage).build());
            alarmCallbacks.forEach(alarmCallback -> {
//            if (alarmCallback.getType().equals("welink")) {
                alarmCallback.doAlarm(alarmMessages, device.getTenantId());
//            }
            });
        }
    }


    public Pager<AlarmDto> getAlarmByPage(AlarmParam alarmParam) {

        QProblem query = buildQproblem(alarmParam);

        PagedList<Problem> problemList = query.setFirstRow((alarmParam.getPage() - 1) * alarmParam.getMaxRow())
                .setMaxRows(alarmParam.getMaxRow()).findPagedList();

        return new Pager<>(formatAlarmList(problemList.getList()), problemList.getTotalCount());
    }

    public List<AlarmDto> getAlarmList(AlarmParam alarmParam) {

        QProblem query = buildQproblem(alarmParam);

        return formatAlarmList(query.findList());
    }

    private QProblem buildQproblem(AlarmParam alarmParam) {
        QProblem query = new QProblem();
        if (ToolUtil.isNotEmpty(alarmParam.getDeviceId())) {
            query.deviceId.eq(alarmParam.getDeviceId());
        }
        if (ToolUtil.isNotEmpty(alarmParam.getTimeFrom())) {
            query.clock.ge(LocalDateTimeUtils.getLDTByMilliSeconds(alarmParam.getTimeFrom() * 1000));
        }
        if (ToolUtil.isNotEmpty(alarmParam.getTimeTill())) {
            query.clock.lt(LocalDateTimeUtils.getLDTByMilliSeconds(alarmParam.getTimeTill() * 1000));
        }
        if (ToolUtil.isNotEmpty(alarmParam.getStatusName())) {
            if (PROBLEM_RESOLVE.equals(alarmParam.getStatusName())) {
                query.rClock.isNotNull();
            } else {
                query.rClock.isNull();
            }
        }
        if (ToolUtil.isNotEmpty(alarmParam.getSeverity())) {
            query.severity.eq(alarmParam.getSeverity());
        }
        query.orderBy().clock.desc();
        return query;
    }

    private List<AlarmDto> formatAlarmList(List<Problem> problemList) {
        List<AlarmDto> list = new ArrayList<>();
        problemList.forEach(problem -> {
            AlarmDto alarmDto = new AlarmDto();
            BeanUtils.copyProperties(problem, alarmDto);
            alarmDto.setSeverity(problem.getSeverity() + "");
            alarmDto.setStatus(problem.getRClock() == null ? "未解决" : "已解决");
            alarmDto.setAcknowledged(problem.getAcknowledged() == 0 ? "未确认" : "已确认");
            list.add(alarmDto);
        });
        return list;
    }

    public void acknowledgement(String eventId) {
        zbxProblem.acknowledgement(eventId, 2);
    }

    public void resolve(String eventId) {
        zbxProblem.acknowledgement(eventId, 1);
    }
}
