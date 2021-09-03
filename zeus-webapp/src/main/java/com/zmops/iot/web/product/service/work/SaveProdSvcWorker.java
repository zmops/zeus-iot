package com.zmops.iot.web.product.service.work;


import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.ProductService;
import com.zmops.iot.domain.product.ProductServiceParam;
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
 * 产品服务创建 同步到设备 步骤
 */
@Slf4j
@Component
public class SaveProdSvcWorker implements IWorker<ProductServiceDto, Boolean> {


    @Override
    public Boolean action(ProductServiceDto productServiceDto, Map<String, WorkerWrapper<?, ?>> map) {
        log.debug("处理产品 新增服务 同步到设备工作…………");

        String prodId = productServiceDto.getSid();

        List<String> deviceIds = new QDevice().select(QDevice.Alias.deviceId).productId.eq(Long.parseLong(prodId)).findSingleAttributeList();

        for (String deviceId : deviceIds) {
            ProductService productService = new ProductService();
            ToolUtil.copyProperties(productServiceDto, productService);
            productService.setSid(deviceId);
            productService.setTemplateId(productServiceDto.getId());
            DB.save(productService);

            if (ToolUtil.isNotEmpty(productServiceDto.getProductServiceParamList())) {
                for (ProductServiceParam productServiceParam : productServiceDto.getProductServiceParamList()) {
                    productServiceParam.setServiceId(productService.getId());
                }
                DB.saveAll(productServiceDto.getProductServiceParamList());
            }
        }

        return true;
    }


    @Override
    public Boolean defaultValue() {
        return true;
    }

}
