package com.zmops.iot.web.product.service.work;


import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.product.ProductAttribute;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.web.product.dto.ProductAttr;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

        for (ProductAttribute productAttribute : list) {
            productAttribute.setName(productAttr.getAttrName());
            productAttribute.setKey(productAttr.getKey());
            productAttribute.setUnits(productAttr.getUnits());
            productAttribute.setSource(productAttr.getSource());
            productAttribute.setValueType(productAttr.getValueType());
        }
        DB.updateAll(list);

        return true;
    }


    @Override
    public Boolean defaultValue() {
        return true;
    }

}
