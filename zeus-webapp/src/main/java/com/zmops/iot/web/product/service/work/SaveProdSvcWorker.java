package com.zmops.iot.web.product.service.work;


import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.ProductServiceParam;
import com.zmops.iot.domain.product.ProductServiceRelation;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.product.dto.ProductServiceDto;
import com.zmops.zeus.server.async.callback.IWorker;
import com.zmops.zeus.server.async.wrapper.WorkerWrapper;
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
    public Boolean action(ProductServiceDto productServiceDto, Map<String, WorkerWrapper> map) {

        String prodId = productServiceDto.getRelationId();

        //查询出 继承了此产品的设备
        List<String> deviceIds = new QDevice().select(QDevice.Alias.deviceId).productId.eq(Long.parseLong(prodId)).findSingleAttributeList();
        if (ToolUtil.isEmpty(deviceIds)) {
            return true;
        }

        List<ProductServiceRelation> productServiceRelationList = new ArrayList<>();
        List<ProductServiceParam> productServiceParamList = new ArrayList<>();
        for (String deviceId : deviceIds) {
            //保存设备与服务的关联关系
            ProductServiceRelation productServiceRelation = new ProductServiceRelation();
            productServiceRelation.setRelationId(deviceId);
            productServiceRelation.setServiceId(productServiceDto.getId());
            productServiceRelation.setInherit("1");
            productServiceRelationList.add(productServiceRelation);

            //保存设备与服务参数的关联关系
            productServiceDto.getProductServiceParamList().forEach(productServiceParam -> {
                ProductServiceParam param = new ProductServiceParam();
                ToolUtil.copyProperties(productServiceParam, param);
                param.setId(null);
                param.setDeviceId(deviceId);
                param.setServiceId(productServiceDto.getId());
                productServiceParamList.add(param);
            });
        }
        DB.saveAll(productServiceRelationList);
        DB.saveAll(productServiceParamList);

        return true;
    }


    @Override
    public Boolean defaultValue() {
        return true;
    }

}
