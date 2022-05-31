package com.zmops.iot.web.product.service.event;


import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.product.ProductAttributeEvent;
import com.zmops.iot.domain.product.query.QProductAttributeEvent;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.event.applicationEvent.ProductAttrCreateEvent;
import com.zmops.iot.web.product.dto.ProductAttr;
import com.zmops.zeus.driver.entity.ZbxItemInfo;
import com.zmops.zeus.driver.service.ZbxItem;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 更新设备属性事件中ZBXID
 */
@Slf4j
@Component
@EnableAsync
public class AsyncAttrEventZbxIdEventHandler implements ApplicationListener<ProductAttrCreateEvent> {

    @Autowired
    ZbxItem zbxItem;

    @Override
    @Async
    public void onApplicationEvent(ProductAttrCreateEvent event) {
        log.debug("AsyncAttrEventZbxIdWorker……");
        ProductAttr productAttr = event.getEventData();
        Long attrId = productAttr.getAttrId();

        //根据name 取出监控项
        List<ZbxItemInfo> itemInfos = JSONObject.parseArray(zbxItem.getItemListByName(attrId + ""), ZbxItemInfo.class);
        if (ToolUtil.isEmpty(itemInfos)) {
            return;
        }

        Map<String, ZbxItemInfo> itemMap = itemInfos.parallelStream().collect(Collectors.toMap(o -> o.getHosts().get(0).getHost(), o -> o));
        //取出继承的属性 并塞入对应的 itemId
        List<ProductAttributeEvent> productAttributeEventList = new QProductAttributeEvent().templateId.eq(attrId).findList();
        for (ProductAttributeEvent productAttributeEvent : productAttributeEventList) {
            if (itemMap.get(productAttributeEvent.getProductId() + "") == null) {
                continue;
            }
            productAttributeEvent.setZbxId(itemMap.get(productAttributeEvent.getProductId() + "").getItemid());
        }

        DB.updateAll(productAttributeEventList);

    }
}
