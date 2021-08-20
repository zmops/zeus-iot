package com.zmops.zeus.iot.server.receiver.tcp.module;

import com.zmops.zeus.iot.server.library.module.ModuleDefine;

/**
 * @author nantian created at 2021/8/17 11:25
 */
public class TcpReceiverModule extends ModuleDefine {

    public static final String NAME = "receiver-tcp";

    public TcpReceiverModule() {
        super(NAME);
    }

    @Override
    public Class[] services() {
        return new Class[0];
    }
}
