package com.zmops.iot.web.device.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.iot.web.device.dto.param.DeviceParam;
import com.zmops.iot.web.device.service.DeviceService;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.ProductTag;
import com.zmops.iot.web.product.dto.ValueMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    @PostMapping("/getDeviceByPage")
    public Pager<DeviceDto> devicePageList(@RequestBody DeviceParam deviceParam) {
        return deviceService.devicePageList(deviceParam);
    }

    /**
     * 设备列表
     *
     * @return
     */
    @PostMapping("/list")
    public ResponseData deviceList(@RequestBody DeviceParam deviceParam) {
        return ResponseData.success(deviceService.deviceList(deviceParam));
    }

    /**
     * 设备创建
     */
    @RequestMapping("/create")
    public ResponseData create(@Validated(BaseEntity.Create.class) @RequestBody DeviceDto deviceDto) {
        QDevice qDevice = new QDevice().or().name.eq(deviceDto.getName());
        if (ToolUtil.isNotEmpty(deviceDto.getDeviceId())) {
            qDevice.deviceId.eq(deviceDto.getDeviceId());
        } else {
            deviceDto.setDeviceId(IdUtil.getSnowflake().nextId() + "");
        }
        int count = qDevice.findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.DEVICE_EXISTS);
        }

        deviceService.checkProductExist(deviceDto);
        String deviceId = deviceService.create(deviceDto);
        deviceDto.setDeviceId(deviceId);
        return ResponseData.success(deviceDto);
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
        deviceService.update(deviceDto);

        return ResponseData.success(deviceDto);
    }


    /**
     * 设备删除
     */
    @RequestMapping("/delete")
    public ResponseData delete(@Validated(BaseEntity.Delete.class) @RequestBody DeviceDto deviceDto) {
        return ResponseData.success(deviceService.delete(deviceDto));
    }

    /**
     * 设备详情
     */
    @GetMapping("/detail")
    public ResponseData prodDetail(@RequestParam("deviceId") Long deviceId) {
        return ResponseData.success(deviceService.deviceDetail(deviceId));
    }

    /**
     * 设备标签列表
     */
    @GetMapping("/tag/list")
    public ResponseData prodTagList(@RequestParam("deviceId") String deviceId) {
        return ResponseData.success(deviceService.deviceTagList(deviceId));
    }

    /**
     * 设备值映射列表
     */
    @GetMapping("/valueMap/list")
    public ResponseData valueMapList(@RequestParam("deviceId") String deviceId) {
        return ResponseData.success(deviceService.valueMapList(deviceId));
    }

    /**
     * 设备创建修改 TAG
     *
     * @return
     */
    @PostMapping("/tag/update")
    public ResponseData deviceTagCreate(@RequestBody @Valid ProductTag productTag) {

        String   deviceId = productTag.getProductId();
        Device device   = new QDevice().deviceId.eq(deviceId).findOne();

        if (null == device) {
            throw new ServiceException(BizExceptionEnum.DEVICE_NOT_EXISTS);
        }

        deviceService.deviceTagCreate(productTag, device.getZbxId());

        return ResponseData.success(productTag);
    }


    /**
     * 设备创建值映射
     *
     * @param valueMap
     * @return
     */
    @PostMapping("/valuemap/update")
    public ResponseData prodValueMapCreate(@RequestBody @Validated(BaseEntity.Create.class) ValueMap valueMap) {

        Device device = new QDevice().deviceId.eq(valueMap.getProductId()).findOne();
        if (null == device) {
            throw new ServiceException(BizExceptionEnum.DEVICE_NOT_EXISTS);
        }
        String response = "";
        if (ToolUtil.isEmpty(valueMap.getValuemapid())) {
            response = deviceService.valueMapCreate(device.getZbxId(), valueMap.getValueMapName(), valueMap.getValueMaps());
        } else {
            response = deviceService.valueMapUpdate(device.getZbxId(), valueMap.getValueMapName(), valueMap.getValueMaps(), valueMap.getValuemapid());
        }

        return ResponseData.success(JSONObject.parseObject(response).getJSONArray("valuemapids").get(0));
    }


    /**
     * 删除 设备值映射
     *
     * @param valueMap
     * @return
     */
    @PostMapping("/valuemap/delete")
    public ResponseData prodValueMapDelete(@RequestBody @Validated(BaseEntity.Delete.class) ValueMap valueMap) {
        String response = deviceService.valueMapDelete(valueMap.getValuemapid());
        return ResponseData.success(JSONObject.parseObject(response).getJSONArray("valuemapids").get(0));
    }

}
