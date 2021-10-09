package com.zmops.iot.web.product.service.work;


import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.ProductStatusFunctionRelation;
import com.zmops.iot.web.product.dto.ProductServiceDto;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yefei
 * <p>
 * 产品服务创建 同步到设备 步骤
 */
@Slf4j
@Component
public class SaveProdStatusTriggerWorker implements IWorker<ProductServiceDto, Boolean> {


    @Override
    public Boolean action(ProductServiceDto productServiceDto, Map<String, WorkerWrapper<?, ?>> map) {
        log.debug("处理产品 新增上下线规则 同步到设备工作…………");

        String prodId = productServiceDto.getRelationId();

        List<String> deviceIds = new QDevice().select(QDevice.Alias.deviceId).productId.eq(Long.parseLong(prodId)).findSingleAttributeList();
        List<ProductStatusFunctionRelation> productStatusFunctionRelationList = new ArrayList<>();
        for (String deviceId : deviceIds) {
            ProductStatusFunctionRelation productStatusFunctionRelation = new ProductStatusFunctionRelation();
            productStatusFunctionRelation.setRelationId(deviceId);
            productStatusFunctionRelation.setRuleId(productServiceDto.getId());
            productStatusFunctionRelation.setInherit("1");
            productStatusFunctionRelationList.add(productStatusFunctionRelation);
        }
        DB.saveAll(productStatusFunctionRelationList);

        return true;
    }


    @Override
    public Boolean defaultValue() {
        return true;
    }

}
