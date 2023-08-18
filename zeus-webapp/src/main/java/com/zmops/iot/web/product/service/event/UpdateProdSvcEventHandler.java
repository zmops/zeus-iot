package com.zmops.iot.web.product.service.event;


import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.ProductServiceParam;
import com.zmops.iot.domain.product.query.QProductServiceParam;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.event.applicationEvent.ProductServiceCreateEvent;
import com.zmops.iot.web.product.dto.ProductServiceDto;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yefei
 * <p>
 * 产品服务修改 同步到设备 步骤
 */
@Slf4j
@Component
public class UpdateProdSvcEventHandler implements ApplicationListener<ProductServiceCreateEvent> {


    @Override
    @Async
    public void onApplicationEvent(ProductServiceCreateEvent event) {
        log.debug("UpdateProdSvcWorker…………");
        ProductServiceDto productServiceDto = event.getEventData();
        String prodId = productServiceDto.getRelationId();

        //查询出继承了此产品的设备
        List<String> deviceIds = new QDevice().select(QDevice.Alias.deviceId).productId.eq(Long.parseLong(prodId)).findSingleAttributeList();
        if (ToolUtil.isEmpty(deviceIds)) {
            return;
        }

        new QProductServiceParam().serviceId.eq(productServiceDto.getId()).deviceId.in(deviceIds).delete();
        //保存设备与服务参数的 关联关系
        List<ProductServiceParam> productServiceParamList = new ArrayList<>();
        for (String deviceId : deviceIds) {
            productServiceDto.getProductServiceParamList().forEach(productServiceParam -> {
                ProductServiceParam param = new ProductServiceParam();
                ToolUtil.copyProperties(productServiceParam, param);
                param.setId(null);
                param.setDeviceId(deviceId);
                param.setServiceId(productServiceDto.getId());
                productServiceParamList.add(param);
            });
        }
        DB.saveAll(productServiceParamList);

    }

}
