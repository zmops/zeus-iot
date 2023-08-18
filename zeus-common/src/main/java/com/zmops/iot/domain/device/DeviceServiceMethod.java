package com.zmops.iot.domain.device;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author yefei
 **/
@Data
@Entity
@Table(name = "device_service_method")
public class DeviceServiceMethod {
    private String deviceId;

    private String url;

    private String method;

    private String ip;

    private Integer port;

    private String topic;
}
