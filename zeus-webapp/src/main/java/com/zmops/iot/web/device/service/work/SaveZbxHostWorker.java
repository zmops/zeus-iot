package com.zmops.iot.web.device.service.work;


import com.zmops.iot.async.callback.IWorker;
import com.zmops.iot.async.wrapper.WorkerWrapper;
import com.zmops.iot.constant.ConstantsContext;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.query.QDeviceGroup;
import com.zmops.iot.domain.product.query.QProduct;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.zeus.driver.service.ZbxHost;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.zmops.iot.web.init.DeviceSatusScriptInit.GLOBAL_HOST_GROUP_CODE;

/**
 * @author yefei
 * <p>
 * 保存至zbx
 */
@Slf4j
@Component
public class SaveZbxHostWorker implements IWorker<DeviceDto, String> {

    @Autowired
    ZbxHost zbxHost;

    @Override
    public String action(DeviceDto deviceDto, Map<String, WorkerWrapper<?, ?>> map) {
        log.debug("处理Zbx host工作…………");
        //设备ID 作为zbx HOST name
        Device device = (Device) map.get("saveDvice").getWorkResult().getResult();
        String host = device.getDeviceId() + "";

        //取出 设备对应的 zbx主机组ID,模板ID
        List<String> hostGrpIds = new QDeviceGroup().select(QDeviceGroup.alias().zbxId).deviceGroupId.in(deviceDto.getDeviceGroupIds()).findSingleAttributeList();
        String templateId = new QProduct().select(QProduct.alias().zbxId).productId.eq(deviceDto.getProductId()).findSingleAttribute();
        hostGrpIds.add(ConstantsContext.getConstntsMap().get(GLOBAL_HOST_GROUP_CODE).toString());

        //保存 zbx主机
        String s = "";
        if (ToolUtil.isNotEmpty(deviceDto.getZbxId())) {
            s = zbxHost.hostUpdate(deviceDto.getZbxId(), hostGrpIds, templateId);
        } else {
            s = zbxHost.hostCreate(host, hostGrpIds, templateId);
        }

        return s;
    }

    @Override
    public String defaultValue() {
        return "";
    }

}
