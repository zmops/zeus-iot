package com.zmops.zeus.iot.server.core.camel;

import lombok.Getter;
import lombok.Setter;

/**
 * @author nantian created at 2021/8/23 18:01
 * <p>
 * 方便大家理解，转换一下 命名
 */

@Getter
@Setter
public class IOTDeviceValue {

    private String deviceId; // 设备ID

    private String deviceAttrKey; // 设备属性Key

    private String deviceAttrValue; // 设备属性值

    private Long deviceTime; // 毫秒，70年到现在时间戳
}
