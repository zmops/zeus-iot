package com.zmops.zeus.iot.server.core.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.zmops.zeus.server.jetty.ArgumentsParseException;
import com.zmops.zeus.server.jetty.JettyJsonHandler;
import com.zmops.zeus.server.library.module.ModuleManager;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author nantian created at 2021/8/20 1:04
 * <p>
 * 设备 告警规则 触发动作，Http 统一入口
 */
@Slf4j
public class DeviceTriggerActionHandler extends JettyJsonHandler {

    private final ModuleManager moduleManager;
    private final Gson          gson = new Gson();

    public DeviceTriggerActionHandler(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @Override
    public String pathSpec() {
        return "/device/action/exec";
    }


    /**
     * 动作触发Http入口，可在设备 上面设置 宏 定义，动态传入 identifier
     *
     * @param req
     * @return JsonElement
     */
    @Override
    public JsonElement doPost(HttpServletRequest req) throws IOException {

        String request = getJsonBody(req);
        log.info("action command ： {}", request);

//        EventBusService eventBusService = moduleManager.find(CoreModule.NAME).provider().getService(EventBusService.class);
//
//
//        ActionParam actionParam = new ActionParam();
//        actionParam.setActionParamContent(request);
//
//        eventBusService.postExecuteActionMsg(ActionRouteIdentifier.helloworld, actionParam);

        return null;
    }


    @Override
    public String getJsonBody(HttpServletRequest req) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        String line = null;
        BufferedReader reader = req.getReader();
        while ((line = reader.readLine()) != null) {
            stringBuffer.append(line);
        }
        return stringBuffer.toString();
    }


}
