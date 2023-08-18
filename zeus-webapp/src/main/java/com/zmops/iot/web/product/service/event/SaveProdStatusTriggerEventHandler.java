package com.zmops.iot.web.product.service.event;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author yefei
 * <p>
 * 产品服务创建 同步到设备 步骤
 */
@Slf4j
@Component
public class SaveProdStatusTriggerEventHandler {//implements IWorker<ProductServiceDto, Boolean>


//    @Override
//    public Boolean action(ProductServiceDto productServiceDto, Map<String, WorkerWrapper> map) {
//
//        String prodId = productServiceDto.getRelationId();
//
//        List<String> deviceIds = new QDevice().select(QDevice.Alias.deviceId).productId.eq(Long.parseLong(prodId)).findSingleAttributeList();
//        List<ProductStatusFunctionRelation> productStatusFunctionRelationList = new ArrayList<>();
//        for (String deviceId : deviceIds) {
//            ProductStatusFunctionRelation productStatusFunctionRelation = new ProductStatusFunctionRelation();
//            productStatusFunctionRelation.setRelationId(deviceId);
//            productStatusFunctionRelation.setRuleId(productServiceDto.getId());
//            productStatusFunctionRelation.setInherit("1");
//            productStatusFunctionRelationList.add(productStatusFunctionRelation);
//        }
//        DB.saveAll(productStatusFunctionRelationList);
//
//        return true;
//    }
//
//
//    @Override
//    public Boolean defaultValue() {
//        return true;
//    }

}
