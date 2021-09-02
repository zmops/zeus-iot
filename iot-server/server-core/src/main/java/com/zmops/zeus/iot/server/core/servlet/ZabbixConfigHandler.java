package com.zmops.zeus.iot.server.core.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zmops.zeus.iot.server.library.server.jetty.ArgumentsParseException;
import com.zmops.zeus.iot.server.library.server.jetty.JettyJsonHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author nantian created at 2021/9/2 10:20
 */
public class ZabbixConfigHandler extends JettyJsonHandler {

    private final String zabbixConfigPath;
    private final Gson   gson = new Gson();

    public ZabbixConfigHandler(String zabbixConfigPath) {
        this.zabbixConfigPath = zabbixConfigPath;
    }

    @Override
    public String pathSpec() {
        return "/zabbix/config";
    }

    @Override
    protected JsonElement doPost(HttpServletRequest req) throws ArgumentsParseException, IOException {

        Properties  prop = new Properties();
        InputStream in   = new FileInputStream(zabbixConfigPath);

        prop.load(in);
        in.close();

        Set<String> confNames = prop.stringPropertyNames();

        Map<String, String> confMap = new HashMap<>();
        confNames.forEach(i -> {
            confMap.put(i, prop.getProperty(i));
        });

        return gson.fromJson(gson.toJson(confMap), JsonObject.class);
    }
}
