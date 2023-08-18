package com.zmops.iot.web.product.service.event;


import cn.hutool.core.util.IdUtil;
import com.zmops.iot.domain.product.ProductAttribute;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.iot.web.event.applicationEvent.ProductModelCreateEvent;
import com.zmops.iot.web.product.dto.ProductAttr;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 产品属性创建 同步到设备 步骤
 */
@Slf4j
@Component
@Order(0)
public class SaveProdAttrEventHandler implements ApplicationListener<ProductModelCreateEvent> {

    private static final String ATTR_SOURCE_DEPEND = "18";

    @Override
    public void onApplicationEvent(ProductModelCreateEvent event) {
        log.debug("SaveProdAttrWorker…………");
        ProductAttr productAttr = event.getEventData();

        String sql = "select device_id from device " +
                " where product_id = :productId and device_id not in (" +
                " select product_id from product_attribute " +
                " where template_id is null and key = :key)";

        List<DeviceDto> deviceDtoList = DB.findDto(DeviceDto.class, sql).setParameter("productId", Long.parseLong(productAttr.getProductId()))
                .setParameter("key", productAttr.getKey()).findList();
        List<ProductAttribute> productAttributeList = new ArrayList<>();

        if (ToolUtil.isEmpty(deviceDtoList)) {
            return;
        }

        //处理依赖属性
        Map<String, Long> attrIdMap = new ConcurrentHashMap<>(deviceDtoList.size());
        if (ATTR_SOURCE_DEPEND.equals(productAttr.getSource())) {
            List<ProductAttribute> list = new QProductAttribute().templateId.eq(productAttr.getDepAttrId()).findList();
            attrIdMap = list.parallelStream().collect(Collectors.toMap(ProductAttribute::getProductId, ProductAttribute::getAttrId, (a, b) -> a));
        }

        for (DeviceDto deviceDto : deviceDtoList) {
            ProductAttribute productAttrbute = new ProductAttribute();
            ToolUtil.copyProperties(productAttr, productAttrbute);
            productAttrbute.setAttrId(IdUtil.getSnowflake().nextId());
            productAttrbute.setName(productAttr.getAttrName());
            productAttrbute.setProductId(deviceDto.getDeviceId());
            productAttrbute.setTemplateId(productAttr.getAttrId());
            if (ATTR_SOURCE_DEPEND.equals(productAttr.getSource()) && null != attrIdMap.get(deviceDto.getDeviceId())) {
                productAttrbute.setDepAttrId(attrIdMap.get(deviceDto.getDeviceId()));
            }
            productAttributeList.add(productAttrbute);
        }
        DB.saveAll(productAttributeList);

    }

}
