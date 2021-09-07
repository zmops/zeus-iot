package com.zmops.iot.web.sys.service;

import com.zmops.iot.domain.sys.query.QSysLoginLog;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.LocalDateTimeUtils;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.sys.dto.SysLoginLogDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yefei
 **/
@Service
public class LoginLogService {

    public Pager<SysLoginLogDto> list(Long beginTime, Long endTime, String logName, int page, int maxRow) {
        QSysLoginLog qSysLoginLog = new QSysLoginLog();
        if (ToolUtil.isNotEmpty(beginTime)) {
            qSysLoginLog.createTime.ge(LocalDateTimeUtils.getLDTByMilliSeconds(beginTime));
        }
        if (ToolUtil.isNotEmpty(endTime)) {
            qSysLoginLog.createTime.le(LocalDateTimeUtils.getLDTByMilliSeconds(endTime));
        }
        if (ToolUtil.isNotEmpty(logName)) {
            qSysLoginLog.logName.eq(logName);
        }
        qSysLoginLog.setFirstRow((page - 1) * maxRow).setMaxRows(maxRow);
        List<SysLoginLogDto> pagedList = qSysLoginLog.orderBy("create_time desc").asDto(SysLoginLogDto.class).findList();
        return new Pager<>(pagedList, qSysLoginLog.findCount());
    }
}
