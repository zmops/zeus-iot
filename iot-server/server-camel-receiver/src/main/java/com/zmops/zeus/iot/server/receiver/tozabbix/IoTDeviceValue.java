package com.zmops.zeus.iot.server.receiver.tozabbix;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author nantian created at 2021/8/23 18:01
 * <p>
 * 方便大家理解，转换一下 命名
 */

@Getter
@Setter
public class IoTDeviceValue {

    private String deviceId; // 设备ID

    private Map<String, String> attributes; // key : value

    private Long clock; // 毫秒，70年到现在时间戳
}
