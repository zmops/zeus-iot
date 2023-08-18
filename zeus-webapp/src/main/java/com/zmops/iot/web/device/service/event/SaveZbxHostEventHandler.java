package com.zmops.iot.web.device.service.event;

import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.constant.ConstantsContext;
import com.zmops.iot.domain.device.query.QDeviceGroup;
import com.zmops.iot.domain.product.query.QProduct;
import com.zmops.iot.domain.proxy.query.QProxy;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.iot.web.event.applicationEvent.DeviceSaveEvent;
import com.zmops.zeus.driver.service.ZbxHost;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.zmops.iot.web.init.DeviceSatusScriptInit.GLOBAL_HOST_GROUP_CODE;

/**
 * @author yefei
 * <p>
 * 保存至zbx
 */
@Slf4j
@Component
@Order(0)
public class SaveZbxHostEventHandler implements ApplicationListener<DeviceSaveEvent> {

    @Autowired
    ZbxHost zbxHost;

    @Override
    public void onApplicationEvent(DeviceSaveEvent event) {
        log.debug("事件step 1:SaveZbxHostWorker----DEVICEID:{}…………", event.getEventData().getDeviceId());
        if (null == event.getEventData()) {
            return;
        }
        DeviceDto deviceDto = event.getEventData();
        //设备ID 作为zbx HOST name
        String host = deviceDto.getDeviceId();

        //取出 设备对应的 zbx主机组ID,模板ID
        List<String> hostGrpIds = new QDeviceGroup().select(QDeviceGroup.alias().zbxId).deviceGroupId.in(
                deviceDto.getDeviceGroupIds()).findSingleAttributeList();
        log.debug("step 1-1:SaveZbxHostWorker----hostGrpIds:{}…………", hostGrpIds.toString());
        String templateId = new QProduct().select(QProduct.alias().zbxId).productId.eq(deviceDto.getProductId())
                .findSingleAttribute();
        log.debug("step 1-2:SaveZbxHostWorker----templateId:{}…………", templateId);
        hostGrpIds.add(ConstantsContext.getConstntsMap().get(GLOBAL_HOST_GROUP_CODE).toString());
        log.debug("step 1-3:SaveZbxHostWorker----hostGrpIds:{}…………", hostGrpIds.toString());
        //保存 zbx主机
        String result = "";
        Long proxyId = deviceDto.getProxyId();
        String zbxProxyId = null;
        if (null != proxyId) {
            zbxProxyId = new QProxy().select(QProxy.alias().zbxId).id.eq(proxyId).findSingleAttribute();
        }
        log.debug("step 1-4:SaveZbxHostWorker----zbxProxyId:{}…………", zbxProxyId);
        if (ToolUtil.isNotEmpty(deviceDto.getZbxId())) {
            result = zbxHost.hostUpdate(deviceDto.getZbxId(), hostGrpIds, templateId, zbxProxyId,
                    deviceDto.getDeviceInterface());
        } else {
            result = zbxHost.hostCreate(host, hostGrpIds, templateId, zbxProxyId, deviceDto.getDeviceInterface());
        }
        String hostid = JSONObject.parseObject(result).getJSONArray("hostids").getString(0);
        deviceDto.setZbxId(hostid);
        log.debug("step 1:SaveZbxHostWorker----DEVICEID:{} complete,hostid:{}", deviceDto.getDeviceId(), hostid);
    }

}
