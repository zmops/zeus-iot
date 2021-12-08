package com.zmops.iot.web.sys.service;

import com.zmops.iot.core.auth.context.LoginContextHolder;
import com.zmops.iot.domain.sys.query.QSysOperationLog;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.LocalDateTimeUtils;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.sys.dto.SysOperationLogDto;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yefei
 **/
@Service
public class OperationLogService {

    public Pager<SysOperationLogDto> list(Long beginTime, Long endTime, String logName, String logType, int page, int maxRow) {
        QSysOperationLog qSysOperationLog = new QSysOperationLog();
        if (ToolUtil.isNotEmpty(beginTime)) {
            qSysOperationLog.createTime.ge(LocalDateTimeUtils.getLDTByMilliSeconds(beginTime));
        }
        if (ToolUtil.isNotEmpty(endTime)) {
            qSysOperationLog.createTime.le(LocalDateTimeUtils.getLDTByMilliSeconds(endTime));
        }
        if (ToolUtil.isNotEmpty(logName)) {
            qSysOperationLog.logName.contains(logName);
        }
        if (ToolUtil.isNotEmpty(logType)) {
            qSysOperationLog.logType.eq(logType);
        }
        Long tenantId = LoginContextHolder.getContext().getUser().getTenantId();
        if (null != tenantId) {
            qSysOperationLog.tenantId.eq(tenantId);
        }
        qSysOperationLog.setFirstRow((page - 1) * maxRow).setMaxRows(maxRow);
        List<SysOperationLogDto> pagedList = qSysOperationLog.orderBy("create_time desc").asDto(SysOperationLogDto.class).findList();
        return new Pager<>(pagedList, qSysOperationLog.findCount());
    }
}
