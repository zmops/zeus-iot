package com.zmops.iot.web.protocol.service;

import com.zmops.iot.domain.protocol.ProtocolComponent;
import com.zmops.iot.domain.protocol.query.QProtocolComponent;
import com.zmops.iot.domain.protocol.query.QProtocolGateway;
import com.zmops.iot.domain.protocol.query.QProtocolGatewayMqtt;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.protocol.dto.ProtocolComponentDto;
import com.zmops.iot.web.protocol.dto.param.ProtocolComponentParam;
import io.ebean.DB;
import io.ebean.PagedList;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yefei
 **/
@Service
public class ProtocolComponentService {

    public Pager<ProtocolComponentDto> getProtocolComponentByPage(ProtocolComponentParam protocolComponentParam) {
        QProtocolComponent qProtocolComponent = new QProtocolComponent();

        if (ToolUtil.isNotEmpty(protocolComponentParam.getName())) {
            qProtocolComponent.name.contains(protocolComponentParam.getName());
        }

        if (ToolUtil.isNotEmpty(protocolComponentParam.getStatus())) {
            qProtocolComponent.status.eq(protocolComponentParam.getStatus());
        }

        PagedList<ProtocolComponent> pagedList = qProtocolComponent.setFirstRow((protocolComponentParam.getPage() - 1) * protocolComponentParam.getMaxRow())
                .setMaxRows(protocolComponentParam.getMaxRow()).findPagedList();

        List<ProtocolComponentDto> protocolComponentDtoList = ToolUtil.convertBean(pagedList.getList(), ProtocolComponentDto.class);

        return new Pager<>(protocolComponentDtoList, pagedList.getTotalCount());
    }

    public List<ProtocolComponent> list(ProtocolComponentParam protocolComponentParam) {
        QProtocolComponent qProtocolComponent = new QProtocolComponent();
        qProtocolComponent.status.eq("2");
        if (ToolUtil.isNotEmpty(protocolComponentParam.getName())) {
            qProtocolComponent.name.contains(protocolComponentParam.getName());
        }

        if (ToolUtil.isNotEmpty(protocolComponentParam.getStatus())) {
            qProtocolComponent.status.eq(protocolComponentParam.getStatus());
        }
        return qProtocolComponent.findList();
    }

    public ProtocolComponent create(ProtocolComponentParam protocolComponentParam) {
        ProtocolComponent protocolComponent = new ProtocolComponent();
        ToolUtil.copyProperties(protocolComponentParam, protocolComponent);
        protocolComponent.setStatus("0");
        DB.insert(protocolComponent);
        return protocolComponent;
    }

    public ProtocolComponent update(ProtocolComponentParam protocolComponentParam) {
        ProtocolComponent protocolComponent = new ProtocolComponent();
        ToolUtil.copyProperties(protocolComponentParam, protocolComponent);
        DB.update(protocolComponent);
        return protocolComponent;
    }

    public void delete(List<Long> protocolComponentIds) {
        int count = new QProtocolGateway().protocolComponentId.in(protocolComponentIds).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PROTOCOL_COMPONENT_HAS_BIND_GATEWAY);
        }

        count = new QProtocolGatewayMqtt().protocolComponentId.in(protocolComponentIds).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PROTOCOL_COMPONENT_HAS_BIND_GATEWAY);
        }

        new QProtocolComponent().protocolComponentId.in(protocolComponentIds).delete();
    }


}
