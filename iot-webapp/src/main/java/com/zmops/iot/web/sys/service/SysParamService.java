package com.zmops.iot.web.sys.service;

import com.zmops.iot.domain.sys.SysConfig;
import com.zmops.iot.domain.sys.query.QSysConfig;
import com.zmops.iot.web.sys.dto.SysParamDto;
import io.ebean.DB;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yefei
 **/
@Service
public class SysParamService {

    public List<SysConfig> list() {
        return new QSysConfig().findList();
    }

    public void update(SysParamDto sysParamDto) {
        Map<Long, SysParamDto.SysParam> sysParamMap  = sysParamDto.getSysParamList().parallelStream().collect(Collectors.toMap(SysParamDto.SysParam::getId, o -> o));
        List<SysConfig>                 sysParamList = list();
        for (SysConfig sysConfig : sysParamList) {
            SysParamDto.SysParam sysParam = sysParamMap.get(sysConfig.getId());
            sysConfig.setName(sysParam.getName());
            sysConfig.setValue(sysParam.getValue());
            sysConfig.setRemark(sysParam.getRemark());
        }
        DB.saveAll(sysParamList);
    }
}
