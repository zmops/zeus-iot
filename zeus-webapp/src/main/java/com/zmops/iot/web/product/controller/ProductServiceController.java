package com.zmops.iot.web.product.controller;

import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.product.dto.ProductServiceDto;
import com.zmops.iot.web.product.dto.param.ProductSvcParam;
import com.zmops.iot.web.product.service.ProductSvcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nantian created at 2021/8/4 16:33
 * <p>
 * 产品物模型： 服务
 */

@RestController
@RequestMapping("/product/service")
public class ProductServiceController {

    @Autowired
    ProductSvcService productSvcService;

    /**
     * 服务分页列表
     */
    @RequestMapping("/getServiceByPage")
    public Pager<ProductServiceDto> getServiceByPage(@Validated @RequestBody ProductSvcParam productSvcParam) {
        return productSvcService.getServiceByPage(productSvcParam);
    }

    /**
     * 服务列表
     */
    @RequestMapping("/list")
    public ResponseData list(@Validated @RequestBody ProductSvcParam productSvcParam) {
        return ResponseData.success(productSvcService.list(productSvcParam));
    }

    /**
     * 根据服务 获取参数列表
     */
    @RequestMapping("/param/list")
    public ResponseData paramList(@RequestParam("serviceId") long serviceId) {
        return ResponseData.success(productSvcService.paramList(serviceId));
    }

    /**
     * 服务创建
     */
    @RequestMapping("/create")
    public ResponseData create(@Validated(BaseEntity.Create.class) @RequestBody ProductServiceDto productServiceDto) {
        productServiceDto.setId(null);
        return ResponseData.success(productSvcService.create(productServiceDto));
    }

    /**
     * 服务修改
     */
    @RequestMapping("/update")
    public ResponseData update(@Validated(BaseEntity.Update.class) @RequestBody ProductServiceDto productServiceDto) {
        return ResponseData.success(productSvcService.update(productServiceDto));
    }

    /**
     * 服务删除
     */
    @RequestMapping("/delete")
    public ResponseData delete(@Validated(BaseEntity.Delete.class) @RequestBody ProductServiceDto productServiceDto) {
        productSvcService.delete(productServiceDto.getIds());
        return ResponseData.success(productServiceDto.getIds());
    }
}
