package com.zmops.iot.web.product.service.work;


import cn.hutool.core.util.IdUtil;
import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.ProductAttributeEvent;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.product.dto.ProductAttrEvent;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yefei
 * <p>
 * 产品属性事件创建 同步到设备 步骤
 */
@Slf4j
@Component
public class SaveProdAttrEventWorker implements IWorker<ProductAttrEvent, Boolean> {


    @Override
    public Boolean action(ProductAttrEvent productAttr, Map<String, WorkerWrapper<?, ?>> map) {
        log.debug("SaveProdAttrEventTriggerWorker…………");

        String prodId = productAttr.getProductId();

        int count = new QDevice().deviceId.eq(prodId).findCount();
        if (count > 0) {
            return true;
        }

        List<String> deviceIds = new QDevice().select(QDevice.Alias.deviceId).productId.eq(Long.parseLong(prodId)).findSingleAttributeList();

        List<ProductAttributeEvent> productAttributeEventList = new ArrayList<>();

        for (String deviceId : deviceIds) {
            ProductAttributeEvent productAttributeEvent = new ProductAttributeEvent();
            ToolUtil.copyProperties(productAttr, productAttributeEvent);
            productAttributeEvent.setAttrId(IdUtil.getSnowflake().nextId());
            productAttributeEvent.setName(productAttr.getAttrName());
            productAttributeEvent.setProductId(deviceId);
            productAttributeEvent.setTemplateId(productAttr.getAttrId());
            productAttributeEventList.add(productAttributeEvent);
        }
        DB.saveAll(productAttributeEventList);

        return true;
    }


    @Override
    public Boolean defaultValue() {
        return true;
    }

}
