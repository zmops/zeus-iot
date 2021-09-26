package com.zmops.iot.message.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author nantian created at 2021/9/26 22:47
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "socketio.server")
public class SocketIoConfig {

    /**
     * 端口号
     */
    private Integer port;

    /**
     * host
     */
    private String host;
}
