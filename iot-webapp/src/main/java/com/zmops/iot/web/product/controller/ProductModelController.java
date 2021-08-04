package com.zmops.iot.web.product.controller;

import com.zmops.iot.model.response.ResponseData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nantian created at 2021/8/3 19:44
 * <p>
 * 产品物模型： 属性 服务 事件
 */

@RestController
@RequestMapping("/product/model")
public class ProductModelController {


    /**
     * 创建 产品物模型 属性
     *
     * @return
     */
    @RequestMapping("/attr/create")
    public ResponseData prodModelAttributeCreate() {
        return ResponseData.success();
    }
}
