package com.zmops.iot.rest.controller;

import com.zmops.iot.domain.device.Device;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.device.dto.param.DeviceParam;
import com.zmops.iot.web.device.dto.param.DeviceParams;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yefei
 * <p>
 * 设备接口
 **/
@RestController
@RequestMapping("/rest/device")
public class DeviceRestController {

    /**
     * 设备分页列表
     *
     * @return
     */
    @PostMapping("/getDeviceByPage")
    public Pager<Device> devicePageList(@RequestBody DeviceParam deviceParam) {

        return new Pager<>();
    }

    /**
     * 设备列表
     *
     * @return
     */
    @PostMapping("/list")
    public ResponseData deviceList(@RequestBody DeviceParams deviceParams) {

        return ResponseData.success();
    }

}
