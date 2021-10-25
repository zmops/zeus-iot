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
import com.zmops.iot.enums.CommonStatus;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.iot.web.product.service.ProductEventRuleService;
import com.zmops.zeus.driver.service.ZbxTrigger;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        log.debug("step 8:处理 其它 工作----DEVICEID:"+deviceDto.getDeviceId()+"…………");

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
        //服务参数
        DB.sqlUpdate("insert into product_service_param (device_id,service_id,key,name,value,remark) SELECT :deviceId,service_id,key,name,value,remark from product_service_param where device_id=:relationId")
                .setParameter("deviceId", deviceId).setParameter("relationId", deviceDto.getProductId() + "").execute();

        //上下线规则关联
        DB.sqlUpdate("insert into product_status_function_relation (relation_id,rule_id,inherit) SELECT :deviceId,rule_id,1 from product_status_function_relation where relation_id=:relationId")
                .setParameter("deviceId", deviceId).setParameter("relationId", deviceDto.getProductId() + "").execute();

        //告警规则关联 并 回填zbx triggerId
        List<ProductEventRuleService.Triggers> triggers = JSONObject.parseArray(zbxTrigger.triggerGetByHost(deviceId), ProductEventRuleService.Triggers.class);

        Map<String, String> map = triggers.parallelStream().collect(Collectors.toMap(ProductEventRuleService.Triggers::getDescription, ProductEventRuleService.Triggers::getTriggerid));

        List<ProductEventRelation> productEventRelationList = new QProductEventRelation().status.eq(CommonStatus.ENABLE.getCode()).relationId.eq(deviceDto.getProductId() + "").findList();
        List<ProductEventRelation> newRelationList = new ArrayList<>();
        for (ProductEventRelation productEventRelation : productEventRelationList) {
            ProductEventRelation newEventRelation = new ProductEventRelation();
            newEventRelation.setRelationId(deviceId);
            newEventRelation.setInherit("1");
            newEventRelation.setZbxId(map.get(productEventRelation.getEventRuleId() + ""));
            newEventRelation.setRemark(productEventRelation.getRemark());
            newEventRelation.setStatus(productEventRelation.getStatus());
            newEventRelation.setEventRuleId(productEventRelation.getEventRuleId());
            newRelationList.add(newEventRelation);
        }
        DB.saveAll(newRelationList);

        DB.saveAll(productEventRelationList);
        //告警执行动作关联
        DB.sqlUpdate("insert into product_event_service (service_id,device_id,execute_device_id,event_rule_id,inherit) SELECT service_id,:deviceId,:executeDeviceId,event_rule_id,1 from product_event_service where relation_id=:relationId")
                .setParameter("deviceId", deviceId).setParameter("executeDeviceId", deviceId).setParameter("relationId", deviceDto.getProductId() + "").execute();

        log.debug("step 8:处理 其它 工作----DEVICEID:"+deviceDto.getDeviceId()+"完成");
        return true;
    }

    @Override
    public Boolean defaultValue() {
        return true;
    }
}
