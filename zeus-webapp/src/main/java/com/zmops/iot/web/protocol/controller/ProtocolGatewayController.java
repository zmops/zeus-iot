package com.zmops.iot.web.protocol.controller;

import com.dtflys.forest.Forest;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.protocol.ProtocolGateway;
import com.zmops.iot.domain.protocol.query.QProtocolComponent;
import com.zmops.iot.domain.protocol.query.QProtocolGateway;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.protocol.dto.ProtocolGatewayDto;
import com.zmops.iot.web.protocol.dto.param.ProtocolGatewayParam;
import com.zmops.iot.web.protocol.service.ProtocolGatewayService;
import com.zmops.zeus.driver.service.ZeusServer;
import io.ebean.DB;
import io.ebean.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 协议网关
 *
 * @author yefei
 **/
@RestController
@RequestMapping("/protocol/gateway")
public class ProtocolGatewayController {

    @Autowired
    ProtocolGatewayService protocolGatewayService;

    @Autowired
    ZeusServer zeusServer;

    /**
     * 协议网关分页列表
     */
    @RequestMapping("/getProtocolGatewayByPage")
    public Pager<ProtocolGatewayDto> getProtocolGatewayByPage(@RequestBody ProtocolGatewayParam protocolGatewayParam) {
        return protocolGatewayService.getProtocolGatewayByPage(protocolGatewayParam);
    }

    /**
     * 协议网关列表
     */
    @RequestMapping("/list")
    public ResponseData list(@RequestBody ProtocolGatewayParam protocolGatewayParam) {
        return ResponseData.success(protocolGatewayService.list(protocolGatewayParam));
    }

    /**
     * 协议网关创建
     */
    @RequestMapping("/create")
    public ResponseData create(@Validated(BaseEntity.Create.class) @RequestBody ProtocolGatewayParam protocolGatewayParam) {
        int count = new QProtocolGateway().name.eq(protocolGatewayParam.getName()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PROTOCOL_COMPONENT_EXISTS);
        }
        count = new QProtocolGateway().protocolServiceId.eq(protocolGatewayParam.getProtocolServiceId()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PROTOCOL_SERVICE_HAS_BIND_COMPONENT);
        }
        if(ToolUtil.isEmpty(protocolGatewayParam.getProtocolGatewayMqttList()) && ToolUtil.isEmpty(protocolGatewayParam.getProtocolComponentId())){
            throw new ServiceException(BizExceptionEnum.PROTOCOL_GATEWAY_HAS_NOT_COMPONENT);
        }
        return ResponseData.success(protocolGatewayService.create(protocolGatewayParam));
    }

    /**
     * 协议网关修改
     */
    @RequestMapping("/update")
    public ResponseData update(@Validated(BaseEntity.Update.class) @RequestBody ProtocolGatewayParam protocolGatewayParam) {
        int count = new QProtocolGateway().name.eq(protocolGatewayParam.getName()).protocolGatewayId.ne(protocolGatewayParam.getProtocolGatewayId()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PROTOCOL_COMPONENT_EXISTS);
        }
        count = new QProtocolGateway().protocolServiceId.eq(protocolGatewayParam.getProtocolServiceId()).protocolGatewayId.ne(protocolGatewayParam.getProtocolGatewayId()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PROTOCOL_SERVICE_HAS_BIND_COMPONENT);
        }
        if(ToolUtil.isEmpty(protocolGatewayParam.getProtocolGatewayMqttList()) && ToolUtil.isEmpty(protocolGatewayParam.getProtocolComponentId())){
            throw new ServiceException(BizExceptionEnum.PROTOCOL_GATEWAY_HAS_NOT_COMPONENT);
        }
        return ResponseData.success(protocolGatewayService.update(protocolGatewayParam));
    }


    /**
     * 协议网关删除
     */
    @RequestMapping("/delete")
    public ResponseData delete(@Validated(BaseEntity.Delete.class) @RequestBody ProtocolGatewayParam protocolGatewayParam) {
        protocolGatewayService.delete(protocolGatewayParam.getProtocolGatewayIds());
        return ResponseData.success(protocolGatewayParam.getProtocolGatewayIds());
    }

    /**
     * 协议网关启动
     */
    @RequestMapping("/start")
    @Transactional
    public ResponseData start(@RequestParam("protocolGatewayId") Long protocolGatewayId) {
        ProtocolGateway protocolGateway = new QProtocolGateway().protocolGatewayId.eq(protocolGatewayId).findOne();
        if (protocolGateway == null) {
            throw new ServiceException(BizExceptionEnum.PROTOCOL_GATEWAY_NOT_EXISTS);
        }
        protocolGateway.setStatus("0");
        DB.update(protocolGateway);

        Map<String, String> params = new HashMap<>(1);
        params.put("routeId", protocolGatewayId + "");
        Forest.post("/protocol/gateway/startRoute").host("127.0.0.1").port(12800).addBody(params,"text/html;charset=utf-8").execute();

        return ResponseData.success();
    }

    /**
     * 协议网关停止
     */
    @RequestMapping("/stop")
    @Transactional
    public ResponseData stop(@RequestParam("protocolGatewayId") Long protocolGatewayId) {
        ProtocolGateway protocolGateway = new QProtocolGateway().protocolGatewayId.eq(protocolGatewayId).findOne();
        if (protocolGateway == null) {
            throw new ServiceException(BizExceptionEnum.PROTOCOL_GATEWAY_NOT_EXISTS);
        }
        protocolGateway.setStatus("1");
        DB.update(protocolGateway);


        Map<String, String> params = new HashMap<>(1);
        params.put("routeId", protocolGatewayId + "");
        Forest.post("/protocol/gateway/stopRoute").host("127.0.0.1").port(12800).addBody(params,"text/html;charset=utf-8").execute();

        return ResponseData.success();
    }

}
