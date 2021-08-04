package com.zmops.iot.web.sys.controller;

import com.zmops.iot.domain.sys.SysOperationLog;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.web.sys.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yefei
 **/
@RestController
@RequestMapping("/log")
public class LogController {

    @Autowired
    private OperationLogService operationLogService;

    /**
     * 查询操作日志列表
     */
    @RequestMapping("/list")
    @ResponseBody
    public Pager<SysOperationLog> list(@RequestParam(required = false) Long beginTime,
                                       @RequestParam(required = false) Long endTime,
                                       @RequestParam(required = false) String logName,
                                       @RequestParam(required = false) String logType,
                                       @RequestParam(required = false, defaultValue = "1") int page,
                                       @RequestParam(required = false, defaultValue = "20") int maxRow) {
        return operationLogService.list(beginTime, endTime, logName, logType, page,maxRow);
    }
}
