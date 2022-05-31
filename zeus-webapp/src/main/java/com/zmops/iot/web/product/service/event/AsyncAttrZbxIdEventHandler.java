package com.zmops.iot.web.product.service.event;


import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.product.ProductAttribute;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.event.applicationEvent.ProductModelCreateEvent;
import com.zmops.iot.web.product.dto.ProductAttr;
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
@Order(1)
public class AsyncAttrZbxIdEventHandler implements ApplicationListener<ProductModelCreateEvent> {

    @Autowired
    ZbxItem zbxItem;

    @Override
    public void onApplicationEvent(ProductModelCreateEvent event) {
        log.debug("AsyncAttrZbxIdWorker……");
        ProductAttr productAttr = event.getEventData();
        Long attrId = productAttr.getAttrId();

        //根据name 取出监控项
        List<ZbxItemInfo> itemInfos = JSONObject.parseArray(zbxItem.getItemListByName(attrId + ""), ZbxItemInfo.class);
        if (ToolUtil.isEmpty(itemInfos)) {
            return;
        }

        Map<String, ZbxItemInfo> itemMap = itemInfos.parallelStream().collect(Collectors.toMap(o -> o.getHosts().get(0).getHost(), o -> o));
        //取出继承的属性 并塞入对应的 itemId
        List<ProductAttribute> productAttributeList = new QProductAttribute().templateId.eq(attrId).findList();
        for (ProductAttribute productAttribute : productAttributeList) {
            productAttribute.setZbxId(itemMap.get(productAttribute.getProductId() + "").getItemid());
        }

        DB.updateAll(productAttributeList);
    }

}
