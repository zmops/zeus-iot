package com.zmops.iot.web.device.service.event;

import cn.hutool.core.util.IdUtil;
import com.zmops.iot.domain.product.ProductAttribute;
import com.zmops.iot.domain.product.ProductAttributeEvent;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.domain.product.query.QProductAttributeEvent;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.iot.web.event.applicationEvent.DeviceSaveEvent;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yefei
 * <p>
 * 设备属性处理步骤
 */
@Slf4j
@Component
@Order(1)
public class SaveAttributeEventHandler implements ApplicationListener<DeviceSaveEvent> {



    private static final String ATTR_TYPE_RELY = "18";

    @Override
    public void onApplicationEvent(DeviceSaveEvent event) {
        log.debug("step 3:saveAttributeWorker----DEVICEID:{}…………", event.getEventData().getDeviceId());
        if (null == event.getEventData()) {
            return;
        }
        DeviceDto deviceDto = event.getEventData();
        String deviceId = deviceDto.getDeviceId();

        if (ToolUtil.isNotEmpty(deviceDto.getEdit()) && "true".equals(deviceDto.getEdit())) {
            //修改
            //没有修改关联的产品 不做处理
            if (deviceDto.getProductId().equals(deviceDto.getOldProductId())) {
                return;
            }
            new QProductAttribute().productId.eq(deviceId).templateId.isNotNull().delete();
            new QProductAttributeEvent().productId.eq(deviceId).templateId.isNotNull().delete();
        }

        //属性
        List<ProductAttribute> productAttributeList = new QProductAttribute().productId.eq(
                deviceDto.getProductId() + "").orderBy(" source::int ").findList();

        List<ProductAttribute> newProductAttributeList = new ArrayList<>();

        /**
         * 处理依赖属性用
         * 遍历属性时 attrKeyMap保存 继承的属性ID 对应的 key
         * attrIdMap 保存 key 对应的 新的属性ID
         */
        Map<Long, String> attrKeyMap = new ConcurrentHashMap<>(productAttributeList.size());
        Map<String, Long> attrIdMap = new ConcurrentHashMap<>(productAttributeList.size());

        for (ProductAttribute productAttribute : productAttributeList) {
            ProductAttribute newProductAttrbute = new ProductAttribute();
            ToolUtil.copyProperties(productAttribute, newProductAttrbute);
            newProductAttrbute.setTemplateId(productAttribute.getAttrId());
            newProductAttrbute.setZbxId("");
            Long attrId = IdUtil.getSnowflake().nextId();
            newProductAttrbute.setAttrId(attrId);
            newProductAttrbute.setProductId(deviceId);
            //处理依赖属性
            if (ATTR_TYPE_RELY.equals(productAttribute.getSource())) {
                String key = attrKeyMap.get(productAttribute.getDepAttrId());
                if (ToolUtil.isNotEmpty(key)) {
                    Long deptAttrId = attrIdMap.get(key);
                    newProductAttrbute.setDepAttrId(deptAttrId);
                }
            } else {
                attrKeyMap.put(productAttribute.getAttrId(), productAttribute.getKey());
                attrIdMap.put(productAttribute.getKey(), attrId);
            }
            newProductAttributeList.add(newProductAttrbute);
        }
        DB.saveAll(newProductAttributeList);

        //属性事件
        List<ProductAttributeEvent> productAttributeEventList = new QProductAttributeEvent().productId.eq(
                deviceDto.getProductId() + "").findList();
        List<ProductAttributeEvent> newProductAttributeEventList = new ArrayList<>();

        for (ProductAttributeEvent productAttributeEvent : productAttributeEventList) {
            ProductAttributeEvent newProductAttrbuteEvent = new ProductAttributeEvent();
            ToolUtil.copyProperties(productAttributeEvent, newProductAttrbuteEvent);
            newProductAttrbuteEvent.setTemplateId(productAttributeEvent.getAttrId());
            newProductAttrbuteEvent.setZbxId("");
            newProductAttrbuteEvent.setAttrId(IdUtil.getSnowflake().nextId());
            newProductAttrbuteEvent.setProductId(deviceId);
            newProductAttributeEventList.add(newProductAttrbuteEvent);
        }
        DB.saveAll(newProductAttributeEventList);

        log.debug("step 3:saveAttributeWorker----DEVICEID:{} complete…………", deviceDto.getDeviceId());
        return;
    }

}
