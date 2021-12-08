package com.zmops.iot.web.device.service.work;


import com.zmops.iot.constant.ConstantsContext;
import com.zmops.iot.domain.device.query.QDeviceGroup;
import com.zmops.iot.domain.product.query.QProduct;
import com.zmops.iot.domain.proxy.query.QProxy;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.zeus.driver.service.ZbxHost;
import com.zmops.zeus.server.async.callback.IWorker;
import com.zmops.zeus.server.async.wrapper.WorkerWrapper;
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
    public String action(DeviceDto deviceDto, Map<String, WorkerWrapper> map) {
        log.debug("step 5:SaveZbxHostWorker----DEVICEID:{}…………", deviceDto.getDeviceId());
        //设备ID 作为zbx HOST name
        String host = deviceDto.getDeviceId();

        //取出 设备对应的 zbx主机组ID,模板ID
        List<String> hostGrpIds = new QDeviceGroup().select(QDeviceGroup.alias().zbxId).deviceGroupId.in(deviceDto.getDeviceGroupIds()).findSingleAttributeList();
        log.debug("step 5-1:SaveZbxHostWorker----hostGrpIds:{}…………", hostGrpIds.toString());
        String templateId = new QProduct().select(QProduct.alias().zbxId).productId.eq(deviceDto.getProductId()).findSingleAttribute();
        log.debug("step 5-2:SaveZbxHostWorker----templateId:{}…………", templateId);
        hostGrpIds.add(ConstantsContext.getConstntsMap().get(GLOBAL_HOST_GROUP_CODE).toString());
        log.debug("step 5-3:SaveZbxHostWorker----hostGrpIds:{}…………", hostGrpIds.toString());
        //保存 zbx主机
        String s = "";
        Long proxyId = deviceDto.getProxyId();
        String zbxProxyId = null;
        if (null != proxyId) {
            zbxProxyId = new QProxy().select(QProxy.alias().zbxId).id.eq(proxyId).findSingleAttribute();
        }
        log.debug("step 5-4:SaveZbxHostWorker----zbxProxyId:{}…………", zbxProxyId);
        if (ToolUtil.isNotEmpty(deviceDto.getZbxId())) {
            s = zbxHost.hostUpdate(deviceDto.getZbxId(), hostGrpIds, templateId, zbxProxyId, deviceDto.getDeviceInterface());
        } else {
            s = zbxHost.hostCreate(host, hostGrpIds, templateId, zbxProxyId, deviceDto.getDeviceInterface());
        }
        log.debug("step 5:SaveZbxHostWorker----DEVICEID:{} complete", deviceDto.getDeviceId());
        return s;
    }

    @Override
    public String defaultValue() {
        return "";
    }

}
