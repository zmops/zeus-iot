package com.zmops.iot.web.product.controller;

import com.zmops.iot.model.response.ResponseData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nantian created at 2021/8/7 11:58
 * <p>
 * 配置告警规则  属性 事件  触发 方法回调
 */
@RestController
@RequestMapping("/product/event/trigger")
public class ProductEventTriggerController {


    @RequestMapping("/create")
    public ResponseData createEventTrigger() {
        return ResponseData.success();
    }



}
