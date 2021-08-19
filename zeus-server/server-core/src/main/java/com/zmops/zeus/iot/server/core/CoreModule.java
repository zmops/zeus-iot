package com.zmops.zeus.iot.server.core;

import com.zmops.zeus.iot.server.core.camel.CamelContextHolderService;
import com.zmops.zeus.iot.server.core.server.JettyHandlerRegister;
import com.zmops.zeus.iot.server.library.module.ModuleDefine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nantian created at 2021/8/16 22:27
 */
public class CoreModule extends ModuleDefine {

    public static final String NAME = "core";

    public CoreModule() {
        super(NAME);
    }

    @Override
    public Class<?>[] services() {
        List<Class<?>> classes = new ArrayList<>();

        classes.add(JettyHandlerRegister.class);
        classes.add(CamelContextHolderService.class);

        return classes.toArray(new Class[]{});
    }
}
