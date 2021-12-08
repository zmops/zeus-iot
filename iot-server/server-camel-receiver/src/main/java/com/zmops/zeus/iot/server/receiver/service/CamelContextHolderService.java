package com.zmops.zeus.iot.server.receiver.service;

import com.zmops.zeus.server.library.module.ModuleManager;
import com.zmops.zeus.server.library.module.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Route;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.impl.EventDrivenConsumerRoute;
import org.apache.camel.model.FromDefinition;

import java.util.List;

/**
 * @author nantian created at 2021/8/17 0:24
 * <p>
 * CamelContextHolderService ，便于模块服务调用，全局只有一个 Camel 上下文
 */
@Slf4j
public class CamelContextHolderService implements Service {

    private final CamelContext camelContext;
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
        EventDrivenConsumerRoute route = (EventDrivenConsumerRoute) camelContext.getRoute(routeId);
        List<FromDefinition> fromDefinitions = camelContext.getRouteDefinition(routeId).getInputs();


        try {
            camelContext.stopRoute(routeId);
            camelContext.removeRoute(routeId);
//            camelContext.removeRouteDefinition(camelContext.getRouteDefinition(routeId));
        } catch (Exception e) {
            e.printStackTrace();
        }


//        if (route == null) {
//            log.error("当前 routeId : {} 对应的路由不存在", routeId);
//            return;
//        }
//        try {
//            route.getEndpoint().stop();
//            route.getConsumer().stop();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        try {
//            camelContext.removeRoute(routeId);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
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
