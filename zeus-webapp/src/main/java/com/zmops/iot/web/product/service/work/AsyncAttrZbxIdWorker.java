package com.zmops.iot.web.product.service.work;


import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.product.ProductAttribute;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.product.dto.ProductAttr;
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
 * 更新设备属性中ZBXID
 */
@Slf4j
@Component
public class AsyncAttrZbxIdWorker implements IWorker<ProductAttr, Boolean> {

    @Autowired
    ZbxItem zbxItem;

    @Override
    public Boolean action(ProductAttr productAttr, Map<String, WorkerWrapper<?, ?>> map) {
        log.debug("处理Attr zbx回填工作……");
        Long attrId = productAttr.getAttrId();

        //根据name 取出监控项
        List<ZbxItemInfo> itemInfos = JSONObject.parseArray(zbxItem.getItemListByName(attrId + ""), ZbxItemInfo.class);
        if (ToolUtil.isEmpty(itemInfos)) {
            return true;
        }

        Map<String, ZbxItemInfo> itemMap = itemInfos.parallelStream().collect(Collectors.toMap(o -> o.getHosts().get(0).getHost(), o -> o));
        //取出继承的属性 并塞入对应的 itemId
        List<ProductAttribute> productAttributeList = new QProductAttribute().templateId.eq(attrId).findList();
        for (ProductAttribute productAttribute : productAttributeList) {
            productAttribute.setZbxId(itemMap.get(productAttribute.getProductId()+"").getItemid());
        }

        DB.updateAll(productAttributeList);
        return true;
    }

    @Override
    public Boolean defaultValue() {
        return true;
    }

}
