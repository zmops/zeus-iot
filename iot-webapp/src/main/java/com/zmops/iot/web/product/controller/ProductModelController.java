package com.zmops.iot.web.product.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductAttr;
import com.zmops.iot.web.product.service.ProductService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author nantian created at 2021/8/3 19:44
 * <p>
 * 产品物模型： 属性 服务 事件
 */

@RestController
@RequestMapping("/product/model")
public class ProductModelController {


    @Autowired
    private ProductService productService;


    /**
     * 创建 产品物模型 属性
     *
     * @return ResponseData
     */
    @RequestMapping("/attr/trapper/create")
    public ResponseData prodModelAttributeCreate(@RequestBody @Valid ProductAttr productAttr) {
        int i = new QProductAttribute().productId.eq(productAttr.getProductId()).key.eq(productAttr.getKey()).findCount();
        if (i > 0) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_ATTR_KEY_EXISTS);
        }

        Long attrId = IdUtil.getSnowflake().nextId();
        productAttr.setAttrId(attrId);

        String  response = productService.createTrapperItem(productAttr);
        Integer zbxId    = JSON.parseObject(response, TemplateIds.class).getItemids()[0];

        productAttr.setZbxId(zbxId);

        productService.createProductAttr(productAttr);

        return ResponseData.success(productAttr);
    }


    @Data
    static class TemplateIds {
        private Integer[] itemids;
    }
}
