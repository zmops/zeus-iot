package com.zmops.iot.rest.controller;

import com.zmops.iot.domain.device.Device;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.web.device.dto.param.DeviceParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yefei
 * <p>
 * 设备日志接口
 **/
@RestController
@RequestMapping("/rest/deviceLog")
public class DeviceLogRestController {

    /**
     * 服务日志分页列表
     *
     * @return
     */
    @PostMapping("/getDeviceByPage")
    public Pager<Device> devicePageList(@RequestBody DeviceParam deviceParam) {

        return new Pager<>();
    }


}
