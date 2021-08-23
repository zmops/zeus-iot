package com.zmops.zeus.iot.server.receiver.mqtt.provider;

import com.zmops.zeus.iot.server.library.module.ModuleConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * @author yefei
 */

@Getter
@Setter
public class MqttReceiverConfig extends ModuleConfig {


    /**
     * Export mqtt port
     */
    private int port = 9020;

    /**
     * Export mqtt ip address
     */
    private String host = "127.0.0.1";
}
