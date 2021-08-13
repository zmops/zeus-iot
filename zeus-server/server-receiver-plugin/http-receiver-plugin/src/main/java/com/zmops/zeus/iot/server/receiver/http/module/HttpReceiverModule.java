package com.zmops.zeus.iot.server.receiver.http.module;

import com.zmops.zeus.iot.server.library.module.ModuleDefine;

/**
 * @author nantian created at 2021/8/14 0:13
 * <p>
 * Http 协议 接收报文
 */
public class HttpReceiverModule extends ModuleDefine {

    public static final String NAME = "receiver-http";

    public HttpReceiverModule() {
        super(NAME);
    }

    @Override
    public Class[] services() {
        return new Class[0];
    }
}
