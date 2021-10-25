package com.zmops.zeus.iot.server.h2.module;

import com.zmops.zeus.iot.server.library.module.ModuleDefine;

/**
 * @author nantian created at 2021/10/24 16:56
 */
public class LocalH2Module extends ModuleDefine {

    public static final String NAME = "local-h2";

    public LocalH2Module() {
        super(NAME);
    }

    @Override
    public Class[] services() {
        return new Class[0];
    }
}
