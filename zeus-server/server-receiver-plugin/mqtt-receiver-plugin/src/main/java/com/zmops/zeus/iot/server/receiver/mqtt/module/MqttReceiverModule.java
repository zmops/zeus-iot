package com.zmops.zeus.iot.server.receiver.mqtt.module;

import com.zmops.zeus.iot.server.library.module.ModuleDefine;

/**
 * @author yefei
 */
public class MqttReceiverModule extends ModuleDefine {

    public static final String NAME = "receiver-mqtt";

    public MqttReceiverModule() {
        super(NAME);
    }

    @Override
    public Class[] services() {
        return new Class[0];
    }
}
