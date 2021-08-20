package com.zmops.zeus.iot.server.core.servlet;

import com.google.gson.JsonElement;
import com.zmops.zeus.iot.server.library.server.jetty.ArgumentsParseException;
import com.zmops.zeus.iot.server.library.server.jetty.JettyJsonHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author nantian created at 2021/8/20 1:04
 * <p>
 * 设备 告警规则 触发动作，Http 统一入口
 */
public class DeviceTriggerActionHandler extends JettyJsonHandler {


    @Override
    public String pathSpec() {
        return "/device/action/exec";
    }


    @Override
    protected JsonElement doPost(HttpServletRequest req) throws ArgumentsParseException, IOException {
        return null;
    }


    /**
     * 执行 Action，进 EventBus，方便订阅处理
     */
    private void execute() {

    }
}
