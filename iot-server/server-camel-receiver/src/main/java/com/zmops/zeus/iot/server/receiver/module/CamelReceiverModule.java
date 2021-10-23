package com.zmops.zeus.iot.server.receiver.module;

import com.zmops.zeus.iot.server.library.module.ModuleDefine;
import com.zmops.zeus.iot.server.receiver.service.CamelContextHolderService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nantian created at 2021/10/23 20:59
 * <p>
 * 通信服务模块，Apache Camel 统一接入
 */
public class CamelReceiverModule extends ModuleDefine {

    public static final String NAME = "camel-receiver";

    public CamelReceiverModule() {
        super(NAME);
    }

    @Override
    public Class<?>[] services() {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(CamelContextHolderService.class);
        return classes.toArray(new Class[]{});
    }
}
