package com.zmops.iot.web.sys.controller;

import com.zmops.iot.model.page.Pager;
import com.zmops.iot.web.auth.Permission;
import com.zmops.iot.web.sys.dto.SysLoginLogDto;
import com.zmops.iot.web.sys.service.LoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yefei
 **/
@RestController
@RequestMapping("/loginLog")
public class LoginLogController {

    @Autowired
    private LoginLogService loginLogService;

    /**
     * 登录日志列表
     */
    @Permission(code = "businessLog")
    @RequestMapping("/getLoginLogByPage")
    @ResponseBody
    public Pager<SysLoginLogDto> list(@RequestParam(required = false) Long beginTime,
                                      @RequestParam(required = false) Long endTime,
                                      @RequestParam(required = false) String logName,
                                      @RequestParam(required = false, defaultValue = "1") int page,
                                      @RequestParam(required = false, defaultValue = "20") int maxRow) {
        return loginLogService.list(beginTime, endTime, logName, page, maxRow);
    }
}
