package com.zmops.iot.web.product.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductAttrDto;
import com.zmops.iot.web.product.dto.ProductAttrEvent;
import com.zmops.iot.web.product.dto.param.ProductAttrParam;
import com.zmops.iot.web.product.service.ProductAttributeEventService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nantian created at 2021/8/3 19:44
 * <p>
 * 产品 属性事件
 */

@RestController
@RequestMapping("/product/attribute/event")
public class ProductAttributeEventController {


    @Autowired
    private ProductAttributeEventService productAttributeEventService;


    /**
     * 产品属性事件 分页列表
     *
     * @return ResponseData
     */
    @RequestMapping("/getAttrEventByPage")
    public Pager<ProductAttrDto> prodAttributeEventList(@RequestBody ProductAttrParam productAttr) {
        return productAttributeEventService.prodAttributeEventList(productAttr);
    }

    /**
     * 属性事件 列表
     *
     * @return ResponseData
     */
    @RequestMapping("/list")
    public ResponseData list(@RequestBody ProductAttrParam productAttr) {
        return ResponseData.success(productAttributeEventService.list(productAttr));
    }

    /**
     * 属性事件 详情
     *
     * @return ResponseData
     */
    @RequestMapping("/detail")
    public ResponseData detail(@RequestParam(value = "attrId") Long attrId) {
        return ResponseData.success(productAttributeEventService.detail(attrId));
    }

    /**
     * 创建 产品属性事件
     *
     * @return ResponseData
     */
    @RequestMapping("/create")
    public ResponseData prodModelAttributeCreate(@RequestBody @Validated(BaseEntity.Create.class) ProductAttrEvent productAttr) {
        int i = new QProductAttribute().productId.eq(productAttr.getProductId()).key.eq(productAttr.getKey()).findCount();
        if (i > 0) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_ATTR_KEY_EXISTS);
        }

        Long attrId = IdUtil.getSnowflake().nextId();
        productAttr.setAttrId(attrId);

        String response = productAttributeEventService.createTrapperItem(productAttr);
        String zbxId = JSON.parseObject(response, TemplateIds.class).getItemids()[0];

        productAttributeEventService.createProductAttr(productAttr, zbxId);

        return ResponseData.success(productAttr);
    }

    /**
     * 修改 产品属性事件
     *
     * @return ResponseData
     */
    @RequestMapping("/update")
    public ResponseData prodModelAttributeUpdate(@RequestBody @Validated(BaseEntity.Update.class) ProductAttrEvent productAttr) {
        int i = new QProductAttribute().productId.eq(productAttr.getProductId()).key.eq(productAttr.getKey())
                .attrId.ne(productAttr.getAttrId()).findCount();
        if (i > 0) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_ATTR_KEY_EXISTS);
        }

        return ResponseData.success(productAttributeEventService.updateTrapperItem(productAttr));
    }

    /**
     * 删除 产品属性事件
     *
     * @return ResponseData
     */
    @RequestMapping("/delete")
    public ResponseData prodModelAttributeDelete(@RequestBody @Validated(BaseEntity.Delete.class) ProductAttrEvent productAttr) {

        productAttributeEventService.deleteTrapperItem(productAttr);
        return ResponseData.success();
    }

    @Data
    static class TemplateIds {
        private String[] itemids;
    }
}
