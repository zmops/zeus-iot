package com.zmops.iot.web.sys.service;

import com.zmops.iot.domain.sys.SysOperationLog;
import com.zmops.iot.domain.sys.query.QSysOperationLog;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.LocalDateTimeUtils;
import com.zmops.iot.util.ToolUtil;
import io.ebean.PagedList;
import org.springframework.stereotype.Service;

/**
 * @author yefei
 **/
@Service
public class OperationLogService {

    public Pager<SysOperationLog> list(Long beginTime, Long endTime, String logName, String logType, int page, int maxRow) {
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
        qSysOperationLog.setFirstRow((page - 1) * maxRow).setMaxRows(maxRow);
        PagedList<SysOperationLog> pagedList = qSysOperationLog.orderBy("create_time desc").findPagedList();
        return new Pager<>(pagedList.getList(), pagedList.getTotalCount());
    }
}
