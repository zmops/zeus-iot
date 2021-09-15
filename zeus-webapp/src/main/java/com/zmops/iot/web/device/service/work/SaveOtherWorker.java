package com.zmops.iot.web.device.service.work;


import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.product.query.QProductServiceRelation;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceDto;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yefei
 * <p>
 * 设备标签处理步骤
 */
@Slf4j
@Component
public class SaveOtherWorker implements IWorker<DeviceDto, Boolean> {

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
        } else {
            //创建
            Device device = (Device) allWrappers.get("saveDvice").getWorkResult().getResult();
            deviceId = device.getDeviceId();
        }

        //服务关联
        DB.sqlUpdate("insert into product_service_relation (relation_id,service_id,inherit) SELECT :deviceId,service_id,1 from product_service_relation where relation_id=:relationId")
                .setParameter("deviceId", deviceId).setParameter("relationId", deviceDto.getProductId()).execute();

        return true;
    }

    @Override
    public Boolean defaultValue() {
        return true;
    }
}
