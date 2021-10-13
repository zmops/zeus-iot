package com.zmops.iot.web.product.service.work;


import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.ProductServiceParam;
import com.zmops.iot.domain.product.query.QProductServiceParam;
import com.zmops.iot.util.ToolUtil;
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
 * 产品服务修改 同步到设备 步骤
 */
@Slf4j
@Component
public class UpdateProdSvcWorker implements IWorker<ProductServiceDto, Boolean> {


    @Override
    public Boolean action(ProductServiceDto productServiceDto, Map<String, WorkerWrapper<?, ?>> map) {
        log.debug("处理产品服务修改 同步到设备工作…………");

        String prodId = productServiceDto.getRelationId();

        List<String> deviceIds = new QDevice().select(QDevice.Alias.deviceId).productId.eq(Long.parseLong(prodId)).findSingleAttributeList();

        List<ProductServiceParam> productServiceParamList = new ArrayList<>();
        for (String deviceId : deviceIds) {
            productServiceDto.getProductServiceParamList().forEach(productServiceParam -> {
                ProductServiceParam param = new ProductServiceParam();
                ToolUtil.copyProperties(productServiceParam, param);
                param.setDeviceId(deviceId);
                param.setServiceId(productServiceDto.getId());
                productServiceParamList.add(param);
            });
        }
        DB.saveAll(productServiceParamList);

        return true;
    }


    @Override
    public Boolean defaultValue() {
        return true;
    }

}
