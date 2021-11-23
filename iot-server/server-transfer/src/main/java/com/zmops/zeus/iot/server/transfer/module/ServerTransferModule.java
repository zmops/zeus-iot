package com.zmops.zeus.iot.server.transfer.module;

import com.zmops.zeus.server.library.module.ModuleDefine;

/**
 * @author nantian created at 2021/9/22 16:39
 */
public class ServerTransferModule extends ModuleDefine {

    public static final String NAME = "server-transfer";


    public ServerTransferModule() {
        super(NAME);
    }

    @Override
    public Class[] services() {
        return new Class[0];
    }
}
