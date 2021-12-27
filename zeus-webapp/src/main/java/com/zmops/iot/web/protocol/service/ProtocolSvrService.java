package com.zmops.iot.web.protocol.service;

import com.dtflys.forest.Forest;
import com.zmops.iot.domain.protocol.ProtocolService;
import com.zmops.iot.domain.protocol.query.QProtocolGateway;
import com.zmops.iot.domain.protocol.query.QProtocolService;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.DefinitionsUtil;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.protocol.dto.ProtocolServiceDto;
import com.zmops.iot.web.protocol.dto.param.ProtocolServiceParam;
import com.zmops.iot.web.protocol.enums.ProtocolEnum;
import io.ebean.DB;
import io.ebean.PagedList;
import io.ebean.annotation.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yefei
 **/
@Service
public class ProtocolSvrService implements CommandLineRunner {

    public Pager<ProtocolServiceDto> getProtocolServiceByPage(ProtocolServiceParam protocolServiceParam) {
        QProtocolService qProtocolService = new QProtocolService();

        if (ToolUtil.isNotEmpty(protocolServiceParam.getName())) {
            qProtocolService.name.contains(protocolServiceParam.getName());
        }

        if (ToolUtil.isNotEmpty(protocolServiceParam.getProtocolType())) {
            qProtocolService.protocolType.eq(protocolServiceParam.getProtocolType());
        }

        PagedList<ProtocolService> pagedList = qProtocolService.setFirstRow((protocolServiceParam.getPage() - 1) * protocolServiceParam.getMaxRow())
                .setMaxRows(protocolServiceParam.getMaxRow()).findPagedList();

        List<ProtocolServiceDto> protocolComponentDtoList = ToolUtil.convertBean(pagedList.getList(), ProtocolServiceDto.class);

        return new Pager<>(protocolComponentDtoList, pagedList.getTotalCount());
    }

    public List<ProtocolService> list(ProtocolServiceParam protocolServiceParam) {
        QProtocolService qProtocolService = new QProtocolService();
        if (ToolUtil.isNotEmpty(protocolServiceParam.getName())) {
            qProtocolService.name.contains(protocolServiceParam.getName());
        }

        if (ToolUtil.isNotEmpty(protocolServiceParam.getProtocolType())) {
            qProtocolService.protocolType.eq(protocolServiceParam.getProtocolType());
        }
        return qProtocolService.findList();
    }

    @Transactional
    public ProtocolService create(ProtocolServiceParam protocolServiceParam) {
        ProtocolService ProtocolService = new ProtocolService();
        ToolUtil.copyProperties(protocolServiceParam, ProtocolService);
        DB.insert(ProtocolService);

        Map<String, String> params = new HashMap<>(1);
        params.put("protocolServiceId", ProtocolService.getProtocolServiceId() + "");
        params.put("name", ProtocolService.getName());
        params.put("url", ProtocolService.getUrl());
        params.put("ip", ProtocolService.getIp());
        params.put("port", ProtocolService.getPort() + "");
        params.put("msgLength", ProtocolService.getMsgLength() + "");
        params.put("clientId", ProtocolService.getClientId());
        params.put("protocol", ProtocolEnum.getDescription(ProtocolService.getProtocolType()));
        params.put("remark", ProtocolService.getRemark());
        Forest.post("/protocol/service/saveProtocolService").host("127.0.0.1").port(12800).addBody(params, "text/html;charset=utf-8").execute();

        updateProtocolServiceEvent();
        return ProtocolService;
    }

    @Transactional
    public ProtocolService update(ProtocolServiceParam protocolServiceParam) {
        ProtocolService ProtocolService = new ProtocolService();
        ToolUtil.copyProperties(protocolServiceParam, ProtocolService);
        DB.update(ProtocolService);

        Map<String, String> params = new HashMap<>(1);
        params.put("protocolServiceId", ProtocolService.getProtocolServiceId() + "");
        params.put("name", ProtocolService.getName());
        params.put("url", ProtocolService.getUrl());
        params.put("ip", ProtocolService.getIp());
        params.put("port", ProtocolService.getPort() + "");
        params.put("msgLength", ProtocolService.getMsgLength() + "");
        params.put("clientId", ProtocolService.getClientId());
        params.put("protocol", ProtocolEnum.getDescription(ProtocolService.getProtocolType()));
        params.put("remark", ProtocolService.getRemark());
        Forest.post("/protocol/service/updateProtocolService").host("127.0.0.1").port(12800).addBody(params, "text/html;charset=utf-8").execute();


        updateProtocolServiceEvent();
        return ProtocolService;
    }

    public void delete(List<Long> protocolServiceIds) {
        int count = new QProtocolGateway().protocolServiceId.in(protocolServiceIds).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PROTOCOL_SERVICE_HAS_BIND_GATEWAY);
        }

        new QProtocolService().protocolServiceId.in(protocolServiceIds).delete();
        updateProtocolServiceEvent();
    }

    public void updateProtocolServiceEvent() {
        List<ProtocolService> serviceList = new QProtocolService().findList();
        Map<Long, String> map = serviceList.parallelStream().collect(Collectors.toMap(ProtocolService::getProtocolServiceId, ProtocolService::getName));
        DefinitionsUtil.updateProtocolServiceCache(map);
    }

    @Override
    public void run(String... args) throws Exception {
        updateProtocolServiceEvent();
    }

}
