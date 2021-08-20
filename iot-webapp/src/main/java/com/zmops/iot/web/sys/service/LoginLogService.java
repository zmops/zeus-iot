package com.zmops.iot.web.sys.service;

import com.zmops.iot.domain.sys.SysLoginLog;
import com.zmops.iot.domain.sys.query.QSysLoginLog;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.LocalDateTimeUtils;
import com.zmops.iot.util.ToolUtil;
import io.ebean.PagedList;
import org.springframework.stereotype.Service;

/**
 * @author yefei
 **/
@Service
public class LoginLogService {

    public Pager<SysLoginLog> list(Long beginTime, Long endTime, String logName, int page, int maxRow) {
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
        PagedList<SysLoginLog> pagedList = qSysLoginLog.orderBy("create_time desc").findPagedList();
        return new Pager<>(pagedList.getList(), pagedList.getTotalCount());
    }
}
