package com.zmops.zeus.iot.server.receiver.service;

import com.zmops.zeus.iot.server.library.module.ModuleManager;
import com.zmops.zeus.iot.server.library.module.Service;
import org.apache.camel.CamelContext;
import org.apache.camel.Route;
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


    /**
     * 关闭路由
     *
     * @param routeId 路由ID
     */
    public void routeShutDown(String routeId) {
        Route route = camelContext.getRoute(routeId);
        try {
            route.getEndpoint().stop();
            route.getConsumer().stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 启动路由
     *
     * @param routeId 路由ID
     */
    public void routeStartUp(String routeId) {
        Route route = camelContext.getRoute(routeId);
        try {
            route.getEndpoint().start();
            route.getConsumer().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
