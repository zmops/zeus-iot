package com.zmops.iot.web.product.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.device.Tag;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.device.query.QTag;
import com.zmops.iot.domain.product.Product;
import com.zmops.iot.domain.product.query.QProduct;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.auth.Permission;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductBasicInfo;
import com.zmops.iot.web.product.dto.ProductDto;
import com.zmops.iot.web.product.dto.ProductTag;
import com.zmops.iot.web.product.dto.ValueMap;
import com.zmops.iot.web.product.service.ProductService;
import io.ebean.DB;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nantian created at 2021/8/3 19:44
 * <p>
 * 产品基本信息维护
 */
@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 产品分页列表
     */
    @Permission(code = "product")
    @PostMapping("/getProductByPage")
    public Pager<ProductDto> getProductByPage(@RequestBody ProductBasicInfo prodBasicInfo) {
        return productService.getProductByPage(prodBasicInfo);
    }

    /**
     * 产品列表
     */
    @Permission(code = "product")
    @PostMapping("/list")
    public ResponseData prodList(@RequestBody ProductBasicInfo prodBasicInfo) {
        return ResponseData.success(productService.prodList(prodBasicInfo));
    }

    /**
     * 产品详情
     */
    @Permission(code = "product")
    @GetMapping("/detail")
    public ResponseData prodDetail(@RequestParam("productId") Long prodId) {
        return ResponseData.success(productService.prodDetail(prodId));
    }

    /**
     * 产品标签列表
     */
    @Permission(code = "product")
    @GetMapping("/tag/list")
    public ResponseData prodTagList(@RequestParam("productId") String prodId) {
        return ResponseData.success(productService.prodTagList(prodId));
    }

    /**
     * 值映射列表
     */
    @Permission(code = "product")
    @GetMapping("/valueMap/list")
    public ResponseData valueMapList(@RequestParam("productId") Long prodId) {
        return ResponseData.success(productService.valueMapList(prodId));
    }

    /**
     * 创建产品
     *
     * @param prodBasicInfo 产品基本信息
     * @return
     */
    @Permission(code = "product")
    @PostMapping("/create")
    public ResponseData prodCreate(@RequestBody @Validated(value = BaseEntity.Create.class) ProductBasicInfo prodBasicInfo) {

        int i = new QProduct().productCode.eq(prodBasicInfo.getProdCode()).findCount();
        if (i > 0) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_HAS_EXIST);
        }

        // 第一步：创建模板
        Long   prodId = IdUtil.getSnowflake().nextId();
        String result = productService.zbxTemplateCreate(prodId + "");

        // 第二步：创建产品
        String zbxId = JSON.parseObject(result, TemplateIds.class).getTemplateids()[0];
        productService.createProduct(zbxId, prodId, prodBasicInfo);
        prodBasicInfo.setProductId(prodId);
        return ResponseData.success(prodBasicInfo);
    }


    /**
     * 更新产品
     *
     * @return
     */
    @Permission(code = "product")
    @PostMapping("/update")
    public ResponseData prodUpdate(@RequestBody @Validated(value = BaseEntity.Update.class)
                                           ProductBasicInfo prodBasicInfo) {
        int i = new QProduct()
                .productCode.eq(prodBasicInfo.getProdCode())
                .productId.ne(prodBasicInfo.getProductId()).findCount();
        if (i > 0) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_HAS_EXIST);
        }

        Product product = productService.updateProduct(prodBasicInfo);

        return ResponseData.success(prodBasicInfo);
    }


    /**
     * 删除产品
     *
     * @param prodBasicInfo 产品基础信息
     * @return
     */
    @Permission(code = "product")
    @PostMapping("/delete")
    public ResponseData prodDelete(@RequestBody @Validated(value = BaseEntity.Delete.class)
                                           ProductBasicInfo prodBasicInfo) {
        //第一步：验证产品下 是否有设备存在
        Product product = new QProduct().productId.eq(prodBasicInfo.getProductId()).findOne();
        if (null == product) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_NOT_EXISTS);
        }
        int deviceNum = new QDevice().productId.eq(prodBasicInfo.getProductId()).findCount();
        if (deviceNum > 0) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_HAS_BIND_DEVICE);
        }


        //第二步：删除Zabbix对应的模板
        String response   = productService.zbxTemplateDelete(product.getZbxId() + "");
        String templateId = JSON.parseObject(response, TemplateIds.class).getTemplateids()[0];
        if (templateId.equals(product.getZbxId())) {
            log.info("产品模板删除成功，ID：{}", templateId);
        }


        //第三步：删除产品
        boolean del = product.delete();
        return ResponseData.success(del);
    }


    /**
     * 产品创建 TAG
     *
     * @return
     */
    @Permission(code = "product")
    @PostMapping("/tag/update")
    public ResponseData prodTagCreate(@RequestBody @Valid ProductTag productTag) {

        String  productId = productTag.getProductId();
        Product product   = new QProduct().productId.eq(Long.parseLong(productId)).findOne();

        if (null == product) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_NOT_EXISTS);
        }

        new QTag().sid.eq(productTag.getProductId()).delete();

        List<Tag> tags = new ArrayList<>();
        for (ProductTag.Tag tag : productTag.getProductTag()) {
            tags.add(Tag.builder()
                    .sid(productTag.getProductId())
                    .tag(tag.getTag()).value(tag.getValue())
                    .build()
            );
        }

        DB.saveAll(tags);

        String response   = productService.updateTemplateTags(product.getZbxId(), productTag);
        String templateId = JSON.parseObject(response, TemplateIds.class).getTemplateids()[0];
        if (templateId.equals(product.getZbxId())) {
            log.info("产品标签修改成功，ID：{}", templateId);
        }

        return ResponseData.success(productTag);
    }


    /**
     * 产品创建值映射
     *
     * @param valueMap
     * @return
     */
    @Permission(code = "product")
    @PostMapping("/valuemap/update")
    public ResponseData prodValueMapCreate(@RequestBody @Validated(BaseEntity.Create.class) ValueMap valueMap) {

        Product product = new QProduct().productId.eq(Long.parseLong(valueMap.getProductId())).findOne();
        if (null == product) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_NOT_EXISTS);
        }
        String response = "";
        if (ToolUtil.isEmpty(valueMap.getValuemapid())) {
            response = productService.valueMapCreate(product.getZbxId(), valueMap.getValueMapName(), valueMap.getValueMaps());
        } else {
            response = productService.valueMapUpdate(product.getZbxId(), valueMap.getValueMapName(), valueMap.getValueMaps(), valueMap.getValuemapid());
        }

        String valuemapid = JSON.parseObject(response, TemplateIds.class).getValuemapids()[0];
        return ResponseData.success(valuemapid);
    }


    /**
     * 删除值映射
     *
     * @param valueMap
     * @return
     */
    @Permission(code = "product")
    @PostMapping("/valueMap/delete")
    public ResponseData prodValueMapDelete(@RequestBody @Validated(BaseEntity.Delete.class) ValueMap valueMap) {
        String response   = productService.valueMapDelete(valueMap.getValuemapid());
        String valuemapid = JSON.parseObject(response, TemplateIds.class).getValuemapids()[0];
        return ResponseData.success(valuemapid);
    }


    @Data
    static class TemplateIds {
        private String[] templateids;
        private String[] valuemapids;
    }
}



