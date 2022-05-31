package com.zmops.zeus.iot.server.core.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zmops.zeus.iot.server.core.worker.data.ItemValue;
import com.zmops.zeus.iot.server.core.worker.data.ZabbixTrapper;
import com.zmops.zeus.iot.server.sender.module.ZabbixSenderModule;
import com.zmops.zeus.iot.server.sender.service.ZabbixSenderService;
import com.zmops.zeus.server.jetty.ArgumentsParseException;
import com.zmops.zeus.server.jetty.JettyJsonHandler;
import com.zmops.zeus.server.library.module.ModuleManager;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author nantian created at 2021/8/20 0:51
 * <p>
 * Http Trapper 的方式给指定设备的指定属性 发送数据，方便手动调试
 */
public class HttpItemTrapperHandler extends JettyJsonHandler {

    private final Gson gson = new Gson();

    private final ModuleManager moduleManager;

    public HttpItemTrapperHandler(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @Override
    public String pathSpec() {
        return "/device/attr/send";
    }

    @Override
    protected JsonElement doPost(HttpServletRequest req) throws ArgumentsParseException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));

        String line;
        StringBuilder request = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            request.append(line);
        }

        JsonObject requestJson = gson.fromJson(request.toString(), JsonObject.class);

        Type listType = new TypeToken<List<ItemValue>>() {}.getType();

        List<ItemValue> valueList = gson.fromJson(requestJson.getAsJsonArray("params").toString(), listType);


        ZabbixSenderService zabbixSenderService = moduleManager.find(ZabbixSenderModule.NAME).provider().getService(ZabbixSenderService.class);
        ZabbixTrapper zabbixTrapper       = new ZabbixTrapper(valueList);
        zabbixSenderService.sendData(gson.toJson(zabbixTrapper));
        return requestJson;
    }
}
