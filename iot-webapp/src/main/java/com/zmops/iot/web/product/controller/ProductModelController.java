package com.zmops.iot.web.product.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.product.ProductAttribute;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductAttr;
import com.zmops.iot.web.product.dto.param.ProductAttrParam;
import com.zmops.iot.web.product.service.ProductModelService;
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
 * 产品物模型： 属性 服务 事件
 */

@RestController
@RequestMapping("/product/model")
public class ProductModelController {


    @Autowired
    private ProductModelService productModelService;


    /**
     * 产品物模型 属性 分页列表
     *
     * @return ResponseData
     */
    @RequestMapping("/getAttrTrapperByPage")
    public Pager<ProductAttr> prodModelAttributeList(@RequestBody ProductAttrParam productAttr) {
        return productModelService.prodModelAttributeList(productAttr);
    }

    /**
     * 产品物模型 属性 列表
     *
     * @return ResponseData
     */
    @RequestMapping("/list")
    public ResponseData list(@RequestBody ProductAttrParam productAttr) {
        return ResponseData.success(productModelService.list(productAttr));
    }

    /**
     * 产品物模型 属性详情
     *
     * @return ResponseData
     */
    @RequestMapping("/detail")
    public ResponseData detail(@RequestParam(value = "attrId") Long attrId) {
        return ResponseData.success(productModelService.detail(attrId));
    }

    /**
     * 创建 产品物模型 属性
     *
     * @return ResponseData
     */
    @RequestMapping("/attr/trapper/create")
    public ResponseData prodModelAttributeCreate(@RequestBody @Validated(BaseEntity.Create.class) ProductAttr productAttr) {
        int i = new QProductAttribute().productId.eq(productAttr.getProductId()).key.eq(productAttr.getKey()).findCount();
        if (i > 0) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_ATTR_KEY_EXISTS);
        }

        Long attrId = IdUtil.getSnowflake().nextId();
        productAttr.setAttrId(attrId);

        String  response = productModelService.createTrapperItem(productAttr);
        String zbxId    = JSON.parseObject(response, TemplateIds.class).getItemids()[0];

        productModelService.createProductAttr(productAttr,zbxId);

        return ResponseData.success(productAttr);
    }

    /**
     * 修改 产品物模型 属性
     *
     * @return ResponseData
     */
    @RequestMapping("/attr/trapper/update")
    public ResponseData prodModelAttributeUpdate(@RequestBody @Validated(BaseEntity.Update.class) ProductAttr productAttr) {
        int i = new QProductAttribute().productId.eq(productAttr.getProductId()).key.eq(productAttr.getKey())
                .attrId.ne(productAttr.getAttrId()).findCount();
        if (i > 0) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_ATTR_KEY_EXISTS);
        }

        return ResponseData.success(productModelService.updateTrapperItem(productAttr));
    }

    /**
     * 删除 产品物模型 属性
     *
     * @return ResponseData
     */
    @RequestMapping("/attr/trapper/delete")
    public ResponseData prodModelAttributeDelete(@RequestBody @Validated(BaseEntity.Delete.class) ProductAttr productAttr) {

        productModelService.deleteTrapperItem(productAttr);
        return ResponseData.success();
    }

    @Data
    static class TemplateIds {
        private String[] itemids;
    }
}
