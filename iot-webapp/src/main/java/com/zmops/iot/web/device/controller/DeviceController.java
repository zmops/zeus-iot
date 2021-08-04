package com.zmops.iot.web.device.controller;

import com.zmops.iot.model.response.ResponseData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nantian created at 2021/8/2 2:05
 * <p>
 * 设备管理
 */
@RestController
@RequestMapping("/device")
public class DeviceController {

    /**
     * 设备分页列表
     *
     * @return
     */
    @RequestMapping("/pagelist")
    public ResponseData devicePageList() {
        return ResponseData.success();
    }
}
