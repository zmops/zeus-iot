package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;
import com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor;

import java.util.Map;

/**
 * @author nantian created at 2021/8/10 16:32
 * <p>
 * 设备离线 在线触发器，判断设备 在线，离线 状态
 */
@BaseRequest(
        baseURL = "${zbxApiUrl}",
        interceptor = JsonBodyBuildInterceptor.class
)
public interface ZbxDeviceStatusTrigger {


    /**
     * 创建 设备 在线，离线 触发器
     *
     * @param triggerRule rule object
     * @return String
     */
    @Post
    @JsonPath("/trigger/device.status.trigger")
    String createDeviceStatusTrigger(@ParamName("rule") Map<String, String> triggerRule);


    /**
     * 修改 设备 在线，离线 触发器
     *
     * @param triggerRule rule object
     * @return String
     */
    @Post
    @JsonPath("/trigger/device.status.trigger.update")
    String updateDeviceStatusTrigger(@ParamName("rule") Map<String, String> triggerRule);
}
