package com.zmops.iot.web.device.controller;

import com.zmops.iot.core.log.BussinessLog;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.device.DeviceGroup;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.device.dto.DeviceGroupDto;
import com.zmops.iot.web.device.dto.param.DeviceGroupParam;
import com.zmops.iot.web.device.service.DeviceGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author nantian created at 2021/8/2 2:06
 * <p>
 * 设备分组管理
 */
@RestController
@RequestMapping("/deviceGroup")
public class DeviceGroupController {
    @Autowired
    DeviceGroupService deviceGroupService;

    /**
     * 设备组分页列表
     *
     * @return
     */
    @PostMapping("/getDeviceGrpByPage")
    public Pager<DeviceGroup> getDeviceGrpByPage(@RequestBody DeviceGroupParam devGroupParam) {
        return deviceGroupService.deviceGroupPageList(devGroupParam);
    }

    /**
     * 设备组列表
     *
     * @return
     */
    @PostMapping("/list")
    public ResponseData deviceGroupList(@RequestBody DeviceGroupParam devGroupParam) {
        return ResponseData.success(deviceGroupService.deviceGroupList(devGroupParam));
    }

    /**
     * 创建设备组
     *
     * @return
     */
    @PostMapping("/create")
    @BussinessLog(value = "创建设备组")
    public ResponseData createDeviceGroup(@Valid @RequestBody DeviceGroupDto deviceGroupDto) {
        return ResponseData.success(deviceGroupService.createDeviceGroup(deviceGroupDto));
    }


    /**
     * 更新设备组
     *
     * @return
     */
    @PostMapping("/update")
    @BussinessLog(value = "更新设备组")
    public ResponseData updateDeviceGroup(@Validated(BaseEntity.Update.class) @RequestBody DeviceGroupDto deviceGroupDto) {
        return ResponseData.success(deviceGroupService.updateDeviceGroup(deviceGroupDto));
    }

    /**
     * 删除设备组
     *
     * @return
     */
    @PostMapping("/delete")
    @BussinessLog(value = "删除设备组")
    public ResponseData deleteDeviceGroup(@Valid @RequestBody DeviceGroupParam deviceGroupParam) {
        deviceGroupService.deleteDeviceGroup(deviceGroupParam);
        return ResponseData.success();
    }
}
