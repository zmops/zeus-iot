package com.zmops.iot.web.product.controller;

import com.zmops.iot.core.log.BussinessLog;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.auth.Permission;
import com.zmops.iot.web.product.dto.param.ProductTypeParam;
import com.zmops.iot.web.product.service.ProductTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yefei
 * <p>
 * 产品分类信息维护
 */
@Slf4j
@RestController
@RequestMapping("/productType")
public class ProductTypeController {

    @Autowired
    private ProductTypeService productTypeService;

    /**
     * 产品分类树
     */
    @Permission(code = "product_type")
    @RequestMapping("/tree")
    public ResponseData tree() {
        return ResponseData.success(productTypeService.tree());
    }

    /**
     * 新增产品分类
     */
    @Permission(code = "product_type")
    @RequestMapping("/create")
    @BussinessLog("新增产品分类")
    public ResponseData create(@Validated(BaseEntity.Create.class) @RequestBody ProductTypeParam productType) {
        return ResponseData.success(productTypeService.create(productType));
    }

    /**
     * 修改产品分类
     */
    @Permission(code = "product_type")
    @RequestMapping("/update")
    @BussinessLog("修改产品分类")
    public ResponseData update(@Validated(BaseEntity.Update.class) @RequestBody ProductTypeParam productType) {
        return ResponseData.success(productTypeService.update(productType));
    }

    /**
     * 删除产品分类
     */
    @Permission(code = "product_type")
    @RequestMapping("/delete")
    @BussinessLog("删除产品分类")
    public ResponseData delete(@Validated(BaseEntity.Delete.class) @RequestBody ProductTypeParam productType) {
        productTypeService.delete(productType);
        return ResponseData.success();
    }
}



