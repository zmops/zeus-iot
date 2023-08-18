package com.zmops.iot.rest.controller;

import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.device.dto.param.DeviceParams;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yefei
 * <p>
 * 登录接口
 **/
@RestController
@RequestMapping("/rest/login")
public class LoginRestController {


    /**
     * 登录
     *
     * @return
     */
    @PostMapping("/list")
    public ResponseData deviceList(@RequestBody DeviceParams deviceParams) {

        return ResponseData.success();
    }

}
