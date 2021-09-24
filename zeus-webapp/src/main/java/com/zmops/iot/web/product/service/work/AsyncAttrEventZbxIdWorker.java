package com.zmops.iot.web.product.service.work;


import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.product.ProductAttributeEvent;
import com.zmops.iot.domain.product.query.QProductAttributeEvent;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.product.dto.ProductAttrEvent;
import com.zmops.zeus.driver.entity.ZbxItemInfo;
import com.zmops.zeus.driver.service.ZbxItem;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AsyncAttrEventZbxIdWorker implements IWorker<ProductAttrEvent, Boolean> {

    @Autowired
    ZbxItem zbxItem;

    @Override
    public Boolean action(ProductAttrEvent productAttr, Map<String, WorkerWrapper<?, ?>> map) {
        log.debug("处理Attr zbx回填工作……");
        Long attrId = productAttr.getAttrId();

        //根据name 取出监控项
        List<ZbxItemInfo> itemInfos = JSONObject.parseArray(zbxItem.getItemListByName(attrId + ""), ZbxItemInfo.class);
        if (ToolUtil.isEmpty(itemInfos)) {
            return true;
        }

        Map<String, ZbxItemInfo> itemMap = itemInfos.parallelStream().collect(Collectors.toMap(o -> o.getHosts().get(0).getHost(), o -> o));
        //取出继承的属性 并塞入对应的 itemId
        List<ProductAttributeEvent> productAttributeEventList = new QProductAttributeEvent().templateId.eq(attrId).findList();
        for (ProductAttributeEvent productAttributeEvent : productAttributeEventList) {
            productAttributeEvent.setZbxId(itemMap.get(productAttributeEvent.getProductId() + "").getItemid());
        }

        DB.updateAll(productAttributeEventList);
        return true;
    }

    @Override
    public Boolean defaultValue() {
        return true;
    }

}
