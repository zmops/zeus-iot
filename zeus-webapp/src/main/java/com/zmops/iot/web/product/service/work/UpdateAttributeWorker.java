package com.zmops.iot.web.product.service.work;


import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.product.ProductAttribute;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.product.dto.ProductAttr;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yefei
 * <p>
 * 产品属性修改 同步到设备 步骤
 */
@Slf4j
@Component
public class UpdateAttributeWorker implements IWorker<ProductAttr, Boolean> {


    @Override
    public Boolean action(ProductAttr productAttr, Map<String, WorkerWrapper<?, ?>> map) {
        log.debug("处理产品属性修改 同步到设备工作…………");

        Long attrId = productAttr.getAttrId();

        List<ProductAttribute> list = new QProductAttribute().templateId.eq(attrId).findList();
        List<ProductAttribute> newList = new ArrayList<>();
        for (ProductAttribute productAttribute : list) {
            ProductAttribute newProductAttribute = new ProductAttribute();
            ToolUtil.copyProperties(productAttribute, newProductAttribute);
            newProductAttribute.setName(productAttr.getAttrName());
            newProductAttribute.setKey(productAttr.getKey());
            newProductAttribute.setUnits(productAttr.getUnits());
            newProductAttribute.setSource(productAttr.getSource());
            newProductAttribute.setValueType(productAttr.getValueType());
            newList.add(newProductAttribute);
        }
        DB.updateAll(newList);

        return true;
    }


    @Override
    public Boolean defaultValue() {
        return true;
    }

}
