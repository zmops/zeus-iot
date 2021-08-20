package com.zmops.zeus.iot.server.core.camel;

import com.zmops.zeus.iot.server.library.module.ModuleManager;
import com.zmops.zeus.iot.server.library.module.Service;
import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;

/**
 * @author nantian created at 2021/8/17 0:24
 * <p>
 * CamelContextHolderService ，便于模块服务调用，全局只有一个 Camel 上下文
 */
public class CamelContextHolderService implements Service {

    private final CamelContext  camelContext;
    private final ModuleManager moduleManager;

    public CamelContextHolderService(CamelContext camelContext, ModuleManager moduleManager) {
        this.camelContext = camelContext;
        this.moduleManager = moduleManager;
    }


    /**
     * 配置路由
     *
     * @param builder 路由
     */
    public void addRoutes(RoutesBuilder builder) {
        try {
            camelContext.addRoutes(builder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
