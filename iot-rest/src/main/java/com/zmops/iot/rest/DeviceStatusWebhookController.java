package com.zmops.iot.rest;

import com.alibaba.fastjson.JSON;
import com.zmops.iot.model.response.ResponseData;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author nantian created at 2021/8/7 14:56
 * <p>
 * 设备在线状态 回调接口
 */

@RestController
@RequestMapping("/rest/device")
public class DeviceStatusWebhookController {


    /**
     * 在线状态 回调
     *
     * @param params webhook 回调参数
     * @return ResponseData
     */
    @RequestMapping("/webhook")
    public ResponseData deviceStatusWebhook(@RequestBody Map<String, String> params) {


        System.out.println(JSON.toJSONString(params));

        return ResponseData.success("OK");
    }
}
