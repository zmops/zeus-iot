package com.zmops.iot.web.protocol.service;

import com.alibaba.fastjson.JSON;
import com.dtflys.forest.Forest;
import com.zmops.iot.domain.protocol.ProtocolGateway;
import com.zmops.iot.domain.protocol.ProtocolGatewayMqtt;
import com.zmops.iot.domain.protocol.ProtocolService;
import com.zmops.iot.domain.protocol.query.QProtocolGateway;
import com.zmops.iot.domain.protocol.query.QProtocolGatewayMqtt;
import com.zmops.iot.domain.protocol.query.QProtocolService;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.protocol.dto.ProtocolGatewayDto;
import com.zmops.iot.web.protocol.dto.param.ProtocolGatewayParam;
import com.zmops.iot.web.protocol.enums.ProtocolEnum;
import io.ebean.DB;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yefei
 **/
@Service
public class ProtocolGatewayService {

    public Pager<ProtocolGatewayDto> getProtocolGatewayByPage(ProtocolGatewayParam protocolGatewayParam) {
        QProtocolGateway qProtocolGateway = new QProtocolGateway();

        if (ToolUtil.isNotEmpty(protocolGatewayParam.getName())) {
            qProtocolGateway.name.contains(protocolGatewayParam.getName());
        }

        if (ToolUtil.isNotEmpty(protocolGatewayParam.getProtocolType())) {
            qProtocolGateway.protocolType.eq(protocolGatewayParam.getProtocolType());
        }

        PagedList<ProtocolGateway> pagedList = qProtocolGateway.setFirstRow((protocolGatewayParam.getPage() - 1) * protocolGatewayParam.getMaxRow())
                .setMaxRows(protocolGatewayParam.getMaxRow()).findPagedList();

        List<ProtocolGatewayDto> protocolGatewayDtoList = ToolUtil.convertBean(pagedList.getList(), ProtocolGatewayDto.class);

        //关联mqtt协议组件
        List<Long> ids = protocolGatewayDtoList.parallelStream().map(ProtocolGatewayDto::getProtocolGatewayId).collect(Collectors.toList());
        List<ProtocolGatewayMqtt> topicList = new QProtocolGatewayMqtt().protocolGatewayId.in(ids).findList();
        Map<Long, List<ProtocolGatewayMqtt>> topicMap = topicList.parallelStream().collect(Collectors.groupingBy(ProtocolGatewayMqtt::getProtocolGatewayId));

        protocolGatewayDtoList.forEach(protocolGatewayDto -> {
            if (ToolUtil.isNotEmpty(topicMap.get(protocolGatewayDto.getProtocolGatewayId()))) {
                protocolGatewayDto.setProtocolGatewayMqttList(topicMap.get(protocolGatewayDto.getProtocolGatewayId()));
            }
        });

        return new Pager<>(protocolGatewayDtoList, pagedList.getTotalCount());
    }

    public List<ProtocolGateway> list(ProtocolGatewayParam protocolGatewayParam) {
        QProtocolGateway qProtocolGateway = new QProtocolGateway();
        if (ToolUtil.isNotEmpty(protocolGatewayParam.getName())) {
            qProtocolGateway.name.contains(protocolGatewayParam.getName());
        }

        if (ToolUtil.isNotEmpty(protocolGatewayParam.getProtocolType())) {
            qProtocolGateway.protocolType.eq(protocolGatewayParam.getProtocolType());
        }
        return qProtocolGateway.findList();
    }

    @Transactional
    public ProtocolGateway create(ProtocolGatewayParam protocolGatewayParam) {
        ProtocolGateway protocolGateway = new ProtocolGateway();
        ToolUtil.copyProperties(protocolGatewayParam, protocolGateway);
        protocolGateway.setStatus("0");
        DB.insert(protocolGateway);

        saveMqtt(protocolGatewayParam.getProtocolGatewayMqttList(), protocolGateway.getProtocolGatewayId());

        Map<String, Object> option = initOptionMap(protocolGatewayParam);

//        ProtocolOption protocolOption = new ProtocolOption();
//        protocolOption.setRouteId(ProtocolGateway.getProtocolGatewayId() + "");
//        protocolOption.setProtocol(ProtocolEnum.getDescription(protocolGatewayParam.getProtocolType()));
//        protocolOption.setOptions(option);

        Map<String, Object> params = new HashMap<>(7);
        params.put("routeId", protocolGateway.getProtocolGatewayId() + "");
        params.put("name", protocolGateway.getName());
        params.put("protocolServiceId", protocolGatewayParam.getProtocolServiceId() + "");
        params.put("protocolComponentId", protocolGatewayParam.getProtocolComponentId() + "");
        params.put("status", protocolGateway.getStatus());
        params.put("protocol", ProtocolEnum.getDescription(protocolGatewayParam.getProtocolType()));
        params.put("option", JSON.toJSONString(option));
        params.put("mqttList", JSON.toJSONString(protocolGatewayParam.getProtocolGatewayMqttList()));

        Forest.post("/protocol/gateway/createProtocolGateway").host("127.0.0.1").port(12800).addBody(params, "text/html;charset=utf-8").execute();
        return protocolGateway;
    }

    private Map<String, Object> initOptionMap(ProtocolGatewayParam protocolGatewayParam) {
        Map<String, Object> option = new HashMap<>(3);
        ProtocolService protocolService = new QProtocolService().protocolServiceId.eq(protocolGatewayParam.getProtocolServiceId()).findOne();
        option.put("hostIp", protocolService.getIp());
        option.put("port", protocolService.getPort());
        if (ToolUtil.isNotEmpty(protocolGatewayParam.getProtocolGatewayMqttList())) {
            String topics = protocolGatewayParam.getProtocolGatewayMqttList().parallelStream().map(ProtocolGatewayMqtt::getTopic).collect(Collectors.joining(","));
            option.put("topicNames", topics);
        }

        option.put("port", protocolService.getPort());
        option.put("mqttList", protocolGatewayParam.getProtocolGatewayMqttList());

        return option;
    }

    @Transactional
    public ProtocolGateway update(ProtocolGatewayParam protocolGatewayParam) {
        ProtocolGateway protocolGateway = new ProtocolGateway();
        ToolUtil.copyProperties(protocolGatewayParam, protocolGateway);
        DB.update(protocolGateway);

        new QProtocolGatewayMqtt().protocolGatewayId.eq(protocolGatewayParam.getProtocolGatewayId()).delete();
        saveMqtt(protocolGatewayParam.getProtocolGatewayMqttList(), protocolGatewayParam.getProtocolGatewayId());

        return protocolGateway;
    }

    private void saveMqtt(List<ProtocolGatewayMqtt> protocolGatewayMqtts, Long protocolGatewayId) {
        if (ToolUtil.isNotEmpty(protocolGatewayMqtts)) {
            List<ProtocolGatewayMqtt> protocolGatewayMqttList = new ArrayList<>();
            protocolGatewayMqtts.forEach(mqtt -> {
                mqtt.setProtocolGatewayId(protocolGatewayId);
                protocolGatewayMqttList.add(mqtt);
            });
            DB.insertAll(protocolGatewayMqttList);
        }
    }

    public void delete(List<Long> protocolGatewayIds) {
//        int count = new QProtocolGateway().protocolServiceId.in(protocolServiceIds).findCount();
//        if (count > 0) {
//            throw new ServiceException(BizExceptionEnum.PROTOCOL_SERVICE_HAS_BIND_GATEWAY);
//        }

        new QProtocolGateway().protocolGatewayId.in(protocolGatewayIds).delete();
        new QProtocolGatewayMqtt().protocolGatewayId.in(protocolGatewayIds).delete();
    }


}
