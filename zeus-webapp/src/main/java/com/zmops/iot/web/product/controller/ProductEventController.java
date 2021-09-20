package com.zmops.iot.web.product.controller;

import com.zmops.iot.model.response.ResponseData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nantian created at 2021/8/7 11:58
 * <p>
 * 产品物模型： 事件
 */
@RestController
@RequestMapping("/product/event")
public class ProductEventController {


    @RequestMapping("/create")
    public ResponseData createEventTrigger() {
        return ResponseData.success();
    }


}
