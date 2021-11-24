package com.zmops.iot.web.product.service.work;


import com.zmops.iot.domain.product.ProductAttribute;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.web.product.dto.ProductAttr;
import com.zmops.zeus.server.async.callback.IWorker;
import com.zmops.zeus.server.async.wrapper.WorkerWrapper;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 产品属性修改 同步到设备 步骤
 */
@Slf4j
@Component
public class UpdateAttributeWorker implements IWorker<ProductAttr, Boolean> {

    private static final String ATTR_SOURCE_DEPEND = "18";

    @Override
    public Boolean action(ProductAttr productAttr, Map<String, WorkerWrapper> map) {
        log.debug("UpdateAttributeWorker…………");

        Long attrId = productAttr.getAttrId();

        List<ProductAttribute> list = new QProductAttribute().templateId.eq(attrId).findList();

        //处理依赖属性
        Map<String, Long> attrIdMap = new ConcurrentHashMap<>(list.size());
        if (ATTR_SOURCE_DEPEND.equals(productAttr.getSource())) {
            List<ProductAttribute> productAttributeList = new QProductAttribute().templateId.eq(productAttr.getDepAttrId()).findList();
            attrIdMap = productAttributeList.parallelStream().collect(Collectors.toMap(ProductAttribute::getProductId, ProductAttribute::getAttrId));
        }

        for (ProductAttribute productAttribute : list) {
            productAttribute.setName(productAttr.getAttrName());
            productAttribute.setKey(productAttr.getKey());
            productAttribute.setUnits(productAttr.getUnits());
            productAttribute.setSource(productAttr.getSource());
            productAttribute.setValueType(productAttr.getValueType());
            productAttribute.setValuemapid(productAttr.getValuemapid());
            if (ATTR_SOURCE_DEPEND.equals(productAttr.getSource()) && null != attrIdMap.get(productAttribute.getProductId())) {
                productAttribute.setDepAttrId(attrIdMap.get(productAttribute.getProductId()));
            }
        }
        DB.updateAll(list);

        return true;
    }


    @Override
    public Boolean defaultValue() {
        return true;
    }

}
