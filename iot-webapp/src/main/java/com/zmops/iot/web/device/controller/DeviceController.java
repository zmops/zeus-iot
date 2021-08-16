package com.zmops.iot.web.device.controller;

import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.Product;
import com.zmops.iot.domain.product.query.QProduct;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.iot.web.device.dto.param.DeviceParam;
import com.zmops.iot.web.device.service.DeviceService;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nantian created at 2021/8/2 2:05
 * <p>
 * 设备管理
 */
@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    DeviceService deviceService;

    /**
     * 设备分页列表
     *
     * @return
     */
    @RequestMapping("/getDeviceByPage")
    public ResponseData devicePageList(@RequestBody DeviceParam deviceParam) {
        return ResponseData.success(deviceService.devicePageList(deviceParam));
    }


    /**
     * 设备创建
     */
    @RequestMapping("/create")
    public ResponseData create(@Validated(BaseEntity.Create.class) @RequestBody DeviceDto deviceDto) {
        int count = new QDevice().name.eq(deviceDto.getName()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.DEVICE_EXISTS);
        }

        deviceService.checkProductExist(deviceDto);

        return ResponseData.success(deviceService.create(deviceDto));
    }

    /**
     * 设备创建
     */
    @RequestMapping("/update")
    public ResponseData update(@Validated(BaseEntity.Update.class) @RequestBody DeviceDto deviceDto) {
        int count = new QDevice().deviceId.ne(deviceDto.getDeviceId()).name.eq(deviceDto.getName()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.DEVICE_EXISTS);
        }

        deviceService.checkProductExist(deviceDto);


        return ResponseData.success(deviceService.update(deviceDto));
    }



}
