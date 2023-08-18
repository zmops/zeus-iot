package com.zmops.zeus.iot.web.config;

import com.zmops.zeus.server.library.web.config.*;

/**
 * @author nantian created at 2021/11/22 20:48
 */
public class IoTConfig extends WebConfig {

    @Override
    public void configConstant(Constants constants) {
        constants.setDevMode(true);
        constants.setMaxPostSize(1024 * 1024 * 100);
        if (!System.getProperty("os.name").toLowerCase().startsWith("win")) {
            constants.setBaseUploadPath("D:\\protocol\\upload");
        } else {
            constants.setBaseUploadPath("//opt//zeus//zeus-iot-bin//upload");
        }
    }

    @Override
    public void configRoute(Routes routes) {
        routes.setBaseViewPath("/pages");
        routes.scan("com.zmops.zeus.iot.web.controller.");
    }

    @Override
    public void configHandler(Handlers handlers) {

    }

    @Override
    public void configInterceptor(Interceptors interceptors) {

    }
}
