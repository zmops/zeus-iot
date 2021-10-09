package com.zmops.iot.web.device.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.product.ProductAttribute;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.device.service.DeviceModelService;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductAttr;
import com.zmops.iot.web.product.dto.ProductAttrDto;
import com.zmops.iot.web.product.dto.param.ProductAttrParam;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yefei
 * <p>
 * 设备物模型
 */

@RestController
@RequestMapping("/device/model")
public class DeviceModelController {


    @Autowired
    private DeviceModelService deviceModelService;

    //依赖属性类型
    private static final String ATTR_SOURCE_DEPEND = "18";

    /**
     * 设备物模型 分页列表
     *
     * @return ResponseData
     */
    @RequestMapping("/getAttrTrapperByPage")
    public Pager<ProductAttrDto> prodModelAttributeList(@Validated(BaseEntity.Get.class) @RequestBody ProductAttrParam productAttr) {
        return deviceModelService.prodModelAttributeList(productAttr);
    }

    /**
     * 设备物模型 列表
     *
     * @return ResponseData
     */
    @RequestMapping("/list")
    public ResponseData list(@RequestBody ProductAttrParam productAttr) {
        return ResponseData.success(deviceModelService.list(productAttr));
    }

    /**
     * 设备物模型 详情
     *
     * @return ResponseData
     */
    @RequestMapping("/detail")
    public ResponseData detail(@RequestParam(value = "attrId") Long attrId) {
        return ResponseData.success(deviceModelService.detail(attrId));
    }

    /**
     * 创建 设备物模型
     *
     * @return ResponseData
     */
    @RequestMapping("/attr/trapper/create")
    public ResponseData prodModelAttributeCreate(@RequestBody @Validated(BaseEntity.Create.class) ProductAttr productAttr) {
        int i = new QProductAttribute().productId.eq(productAttr.getProductId()).key.eq(productAttr.getKey()).findCount();
        if (i > 0) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_ATTR_KEY_EXISTS);
        }

        if (ATTR_SOURCE_DEPEND.equals(productAttr.getSource())) {
            if (productAttr.getDepAttrId() == null) {
                throw new ServiceException(BizExceptionEnum.PRODUCT_ATTR_DEPTED_NULL);
            }
            ProductAttribute productAttribute = new QProductAttribute().attrId.eq(productAttr.getDepAttrId()).findOne();
            if (null == productAttribute) {
                throw new ServiceException(BizExceptionEnum.PRODUCT_ATTR_DEPTED_NOT_EXIST);
            }
            productAttr.setMasterItemId(productAttribute.getZbxId());
        }

        Long attrId = IdUtil.getSnowflake().nextId();
        productAttr.setAttrId(attrId);

        String response = deviceModelService.createTrapperItem(productAttr);
        String zbxId = JSON.parseObject(response, TemplateIds.class).getItemids()[0];

        deviceModelService.createProductAttr(productAttr, zbxId);

        return ResponseData.success(productAttr);
    }

    /**
     * 修改 设备物模型
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

        if (ATTR_SOURCE_DEPEND.equals(productAttr.getSource())) {
            if (productAttr.getDepAttrId() == null) {
                throw new ServiceException(BizExceptionEnum.PRODUCT_ATTR_DEPTED_NULL);
            }
            ProductAttribute productAttribute = new QProductAttribute().attrId.eq(productAttr.getDepAttrId()).findOne();
            if (null == productAttribute) {
                throw new ServiceException(BizExceptionEnum.PRODUCT_ATTR_DEPTED_NOT_EXIST);
            }
            productAttr.setMasterItemId(productAttribute.getZbxId());
        }

        return ResponseData.success(deviceModelService.updateTrapperItem(productAttr));
    }

    /**
     * 删除 设备物模型 属性
     *
     * @return ResponseData
     */
    @RequestMapping("/attr/trapper/delete")
    public ResponseData prodModelAttributeDelete(@RequestBody @Validated(BaseEntity.Delete.class) ProductAttr productAttr) {

        deviceModelService.deleteTrapperItem(productAttr);
        return ResponseData.success();
    }

    @Data
    static class TemplateIds {
        private String[] itemids;
    }
}
