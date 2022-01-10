package com.zmops.iot.web.protocol.controller;

import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.protocol.query.QProtocolService;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.protocol.dto.ProtocolServiceDto;
import com.zmops.iot.web.protocol.dto.param.ProtocolServiceParam;
import com.zmops.iot.web.protocol.service.ProtocolSvrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 协议服务
 *
 * @author yefei
 **/
@RestController
@RequestMapping("/protocol/service")
public class ProtocolServiceController {

    @Autowired
    ProtocolSvrService protocolSvrService;

    /**
     * 协议服务分页列表
     */
    @RequestMapping("/getProtocolServiceByPage")
    public Pager<ProtocolServiceDto> getProtocolComponentByPage(@RequestBody ProtocolServiceParam protocolServiceParam) {
        return protocolSvrService.getProtocolServiceByPage(protocolServiceParam);
    }

    /**
     * 协议服务列表
     */
    @RequestMapping("/list")
    public ResponseData list(@RequestBody ProtocolServiceParam protocolServiceParam) {
        return ResponseData.success(protocolSvrService.list(protocolServiceParam));
    }


    /**
     * 协议服务创建
     */
    @RequestMapping("/create")
    public ResponseData create(@Validated(BaseEntity.Create.class) @RequestBody ProtocolServiceParam protocolServiceParam) {
        int count = new QProtocolService().name.eq(protocolServiceParam.getName()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PROTOCOL_SERVICE_EXISTS);
        }
        count = new QProtocolService().ip.eq(protocolServiceParam.getIp()).port.eq(protocolServiceParam.getPort()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PROTOCOL_SERVICE_PORT_EXISTS);
        }
        return ResponseData.success(protocolSvrService.create(protocolServiceParam));
    }

    /**
     * 协议服务修改
     */
    @RequestMapping("/update")
    public ResponseData update(@Validated(BaseEntity.Update.class) @RequestBody ProtocolServiceParam protocolServiceParam) {
        int count = new QProtocolService().name.eq(protocolServiceParam.getName()).protocolServiceId.ne(protocolServiceParam.getProtocolServiceId()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PROTOCOL_SERVICE_EXISTS);
        }
        count = new QProtocolService().ip.eq(protocolServiceParam.getIp()).port.eq(protocolServiceParam.getPort()).protocolServiceId.ne(protocolServiceParam.getProtocolServiceId()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PROTOCOL_SERVICE_PORT_EXISTS);
        }
        return ResponseData.success(protocolSvrService.update(protocolServiceParam));
    }


    /**
     * 协议服务删除
     */
    @RequestMapping("/delete")
    public ResponseData delete(@Validated(BaseEntity.Delete.class) @RequestBody ProtocolServiceParam protocolServiceParam) {
        protocolSvrService.delete(protocolServiceParam.getProtocolServiceIds());
        return ResponseData.success(protocolServiceParam.getProtocolServiceIds());
    }

}
