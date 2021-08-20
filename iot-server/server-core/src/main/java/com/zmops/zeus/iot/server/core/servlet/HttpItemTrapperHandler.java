package com.zmops.zeus.iot.server.core.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zmops.zeus.iot.server.library.server.jetty.ArgumentsParseException;
import com.zmops.zeus.iot.server.library.server.jetty.JettyJsonHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author nantian created at 2021/8/20 0:51
 * <p>
 * Http Trapper 的方式给指定设备的指定属性 发送数据，方便手动调试
 */
public class HttpItemTrapperHandler extends JettyJsonHandler {

    private final Gson gson = new Gson();

    @Override
    public String pathSpec() {
        return "/device/attr/send";
    }

    @Override
    protected JsonElement doPost(HttpServletRequest req) throws ArgumentsParseException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));

        String        line;
        StringBuilder request = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            request.append(line);
        }

        JsonObject requestJson = gson.fromJson(request.toString(), JsonObject.class);

        return requestJson;
    }
}
