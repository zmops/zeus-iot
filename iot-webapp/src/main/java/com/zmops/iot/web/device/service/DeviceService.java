package com.zmops.iot.web.device.service;

import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.iot.web.device.dto.param.DeviceParam;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yefei
 **/
@Service
public class DeviceService {


    /**
     * 设备分页列表
     *
     * @param deviceParam
     * @return
     */
    public Pager<DeviceDto> devicePageList(DeviceParam deviceParam) {

        QDevice qDevice = new QDevice();

        if (null != deviceParam.getDeviceGroupId()) {
            qDevice.deviceGroupId.eq(deviceParam.getDeviceGroupId());
        }
        if (ToolUtil.isNotEmpty(deviceParam.getProdType())) {
            qDevice.type.eq(deviceParam.getProdType());
        }
        if (ToolUtil.isNotEmpty(deviceParam.getProductId())) {
            qDevice.productId.eq(deviceParam.getProductId());
        }
        if (ToolUtil.isNotEmpty(deviceParam.getName())) {
            qDevice.name.contains(deviceParam.getName());
        }

        List<DeviceDto> list = qDevice.asDto(DeviceDto.class).setFirstRow((deviceParam.getPage() - 1) * deviceParam.getMaxRow())
                .setMaxRows(deviceParam.getMaxRow()).findList();

        return new Pager<>(list, qDevice.findCount());
    }

}
