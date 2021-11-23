package com.zmops.zeus.iot.server.core;

import com.zmops.zeus.server.library.module.ModuleDefine;

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
        return classes.toArray(new Class[]{});
    }
}
