package com.zmops.iot.web.product.service.work;


import cn.hutool.core.util.IdUtil;
import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.ProductAttribute;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.product.dto.ProductAttr;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yefei
 * <p>
 * 产品属性创建 同步到设备 步骤
 */
@Slf4j
@Component
public class SaveProdAttrWorker implements IWorker<ProductAttr, Boolean> {


    @Override
    public Boolean action(ProductAttr productAttr, Map<String, WorkerWrapper<?, ?>> map) {
        log.debug("处理产品 新增Attr 同步到设备工作…………");

        Long prodId = productAttr.getProductId();

        List<Long> deviceIds = new QDevice().select(QDevice.Alias.deviceId).productId.eq(prodId).findSingleAttributeList();

        List<ProductAttribute> productAttributeList = new ArrayList<>();

        for (Long deviceId : deviceIds) {
            ProductAttribute productAttrbute = new ProductAttribute();
            ToolUtil.copyProperties(productAttr, productAttrbute);
            productAttrbute.setAttrId(IdUtil.getSnowflake().nextId());
            productAttrbute.setProductId(deviceId);
            productAttributeList.add(productAttrbute);
        }
        DB.saveAll(productAttributeList);

        return true;
    }


    @Override
    public Boolean defaultValue() {
        return true;
    }

}
