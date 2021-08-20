package com.zmops.iot.web.sys.controller;

import com.zmops.iot.core.log.BussinessLog;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.sys.dto.SysParamDto;
import com.zmops.iot.web.sys.service.SysParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author nantian created at 2021/8/1 21:57
 * <p>
 * 系统参数
 */
@RestController
@RequestMapping("/sys/param")
public class SysParamController {

    @Autowired
    SysParamService sysParamService;

    /**
     * 参数列表
     */
    @GetMapping("/list")
    public ResponseData list() {
        return ResponseData.success(sysParamService.list());
    }

    /**
     * 参数修改
     */
    @PostMapping("/update")
    @BussinessLog(value = "系统参数修改")
    public ResponseData update(@Validated @RequestBody SysParamDto sysParamDto) {
        sysParamService.update(sysParamDto);
        return ResponseData.success();
    }
}
