package com.zmops.iot.web.device.controller;

import com.zmops.iot.domain.device.ApiParam;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.proxy.Proxy;
import com.zmops.iot.domain.proxy.query.QProxy;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.util.LocalDateTimeUtils;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.zeus.driver.entity.ItemParam;
import com.zmops.zeus.driver.entity.SendParam;
import com.zmops.zeus.driver.service.ZbxConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yefei
 **/
@RequestMapping("/device/api")
@RestController
public class DeviceApiController {

    @Autowired
    ZbxConfig zbxConfig;

    //@Value("${forest.variables.zeusServerIp}")
    private String IOT_SERVER_IP = "127.0.0.1";

    @RequestMapping("/sendData")
    public ResponseData sendData(@Validated @RequestBody ApiParam apiParam) {
        Device device = new QDevice().deviceId.eq(apiParam.getDeviceId()).findOne();
        if (null == device) {
            throw new ServiceException(BizExceptionEnum.DEVICE_NOT_EXISTS);
        }
        if (null != device.getProxyId()) {
            Proxy proxy = new QProxy().id.eq(device.getProxyId()).findOne();
            IOT_SERVER_IP = proxy.getAddress();
        }

        List<ItemParam> valueList = new ArrayList<>();
        if (ToolUtil.isNotEmpty(apiParam.getParams())) {
            apiParam.getParams().forEach(params -> {
                if (ToolUtil.isEmpty(params.getDeviceAttrKey()) || ToolUtil.isEmpty(params.getDeviceAttrValue())) {
                    return;
                }
                ItemParam itemParam = new ItemParam();
                itemParam.setHost(apiParam.getDeviceId());
                itemParam.setClock(LocalDateTimeUtils.getSecondsByTime(LocalDateTime.now()));

                itemParam.setKey(params.getDeviceAttrKey());
                itemParam.setValue(params.getDeviceAttrValue());
                valueList.add(itemParam);
            });
        }
        if (ToolUtil.isEmpty(valueList)) {
            throw new ServiceException(BizExceptionEnum.ZBX_DEVICE_API_HASNOT_KEY);
        }
        SendParam sendParam = new SendParam();
        sendParam.setParams(valueList);
        zbxConfig.sendData(IOT_SERVER_IP, sendParam);
        return ResponseData.success();
    }
}
