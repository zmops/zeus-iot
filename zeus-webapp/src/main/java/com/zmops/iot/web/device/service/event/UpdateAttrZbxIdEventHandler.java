package com.zmops.iot.web.device.service.event;

import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.product.ProductAttribute;
import com.zmops.iot.domain.product.ProductAttributeEvent;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.domain.product.query.QProductAttributeEvent;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.iot.web.event.applicationEvent.DeviceSaveEvent;
import com.zmops.zeus.driver.entity.ZbxItemInfo;
import com.zmops.zeus.driver.service.ZbxItem;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 更新设备属性中ZBXID
 */
@Slf4j
@Component
@Order(2)
public class UpdateAttrZbxIdEventHandler implements ApplicationListener<DeviceSaveEvent> {

    @Autowired
    ZbxItem zbxItem;

    @Override
    public void onApplicationEvent(DeviceSaveEvent event) {
        log.debug("step 7:resolve Attr zbxID async----deviceid: {} …………", event.getEventData().getDeviceId());
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
        }

        //取出 ZBX hostid

        //根据hostid 取出监控项
        List<ZbxItemInfo> itemInfos = JSONObject.parseArray(zbxItem.getItemInfo(null, deviceDto.getZbxId()),
                ZbxItemInfo.class);
        if (ToolUtil.isEmpty(itemInfos)) {
            return;
        }

        Map<String, ZbxItemInfo> itemMap = itemInfos.parallelStream()
                .collect(Collectors.toMap(ZbxItemInfo::getName, o -> o, (a, b) -> a));
        //取出继承的属性 并塞入对应的 itemId
        List<ProductAttribute> productAttributeList = new QProductAttribute().productId.eq(deviceId).findList();
        for (ProductAttribute productAttribute : productAttributeList) {
            productAttribute.setZbxId(itemMap.get(productAttribute.getTemplateId() + "").getItemid());
        }

        DB.updateAll(productAttributeList);

        //取出继承的属性事件 并塞入对应的 itemId
        List<ProductAttributeEvent> productAttributeEventList = new QProductAttributeEvent().productId.eq(deviceId)
                .findList();
        for (ProductAttributeEvent productAttributeEvent : productAttributeEventList) {
            productAttributeEvent.setZbxId(itemMap.get(productAttributeEvent.getTemplateId() + "").getItemid());
        }

        DB.updateAll(productAttributeEventList);
        log.debug("step 7:resolve Attr zbxID async----deviceid: {} complete", deviceDto.getDeviceId());
    }

}
