package com.zmops.iot.web.device.service.work;


import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.product.ProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEventRelation;
import com.zmops.iot.domain.product.query.QProductEventService;
import com.zmops.iot.domain.product.query.QProductServiceRelation;
import com.zmops.iot.domain.product.query.QProductStatusFunctionRelation;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.iot.web.product.service.ProductEventRuleService;
import com.zmops.zeus.driver.service.ZbxTrigger;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 设备标签处理步骤
 */
@Slf4j
@Component
public class SaveOtherWorker implements IWorker<DeviceDto, Boolean> {

    @Autowired
    ZbxTrigger zbxTrigger;

    @Override
    public Boolean action(DeviceDto deviceDto, Map<String, WorkerWrapper<?, ?>> allWrappers) {
        log.debug("处理 其它 工作…………");

        String deviceId = deviceDto.getDeviceId();

        if (ToolUtil.isNotEmpty(deviceDto.getEdit()) && "true".equals(deviceDto.getEdit())) {
            //修改

            //没有修改关联的产品 不做处理
            if (deviceDto.getProductId().equals(deviceDto.getOldProductId())) {
                return true;
            }
            //删除服务关联
            new QProductServiceRelation().relationId.eq(deviceId).delete();
            //删除 上下线规则关联
            new QProductStatusFunctionRelation().relationId.eq(deviceId).delete();
            //删除 告警规则关联
            new QProductEventRelation().relationId.eq(deviceId).delete();
            //删除 告警执行动作关联
            new QProductEventService().deviceId.eq(deviceId).delete();

        } else {
            //创建
            Device device = (Device) allWrappers.get("saveDvice").getWorkResult().getResult();
            deviceId = device.getDeviceId();
        }

        //服务关联
        DB.sqlUpdate("insert into product_service_relation (relation_id,service_id,inherit) SELECT :deviceId,service_id,1 from product_service_relation where relation_id=:relationId")
                .setParameter("deviceId", deviceId).setParameter("relationId", deviceDto.getProductId() + "").execute();

        //上下线规则关联
        DB.sqlUpdate("insert into product_status_function_relation (relation_id,rule_id,inherit) SELECT :deviceId,rule_id,1 from product_status_function_relation where relation_id=:relationId")
                .setParameter("deviceId", deviceId).setParameter("relationId", deviceDto.getProductId() + "").execute();

        //告警规则关联 并 回填zbx triggerId
        List<ProductEventRuleService.Triggers> triggers = JSONObject.parseArray(zbxTrigger.triggerGetByHost(deviceId), ProductEventRuleService.Triggers.class);

        Map<String, Integer> map = triggers.parallelStream().collect(Collectors.toMap(ProductEventRuleService.Triggers::getDescription, ProductEventRuleService.Triggers::getTriggerid));

        List<ProductEventRelation> productEventRelationList = new QProductEventRelation().relationId.eq(deviceDto.getProductId() + "").findList();

        for (ProductEventRelation productEventRelation : productEventRelationList) {
            productEventRelation.setId(null);
            productEventRelation.setRelationId(deviceId);
            productEventRelation.setInherit("1");
            productEventRelation.setZbxId(productEventRelation.getZbxId());
        }

        DB.saveAll(productEventRelationList);
        //告警执行动作关联
        DB.sqlUpdate("insert into product_event_service (service_id,device_id,execute_device_id,event_rule_id,inherit) SELECT service_id,:deviceId,:executeDeviceId,event_rule_id,1 from product_event_service where relation_id=:relationId")
                .setParameter("deviceId", deviceId).setParameter("executeDeviceId", deviceId).setParameter("relationId", deviceDto.getProductId() + "").execute();


        return true;
    }

    @Override
    public Boolean defaultValue() {
        return true;
    }
}
