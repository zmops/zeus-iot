package com.zmops.zeus.server.jetty;

import com.google.gson.JsonElement;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public abstract class JettyJsonHandler {
    public abstract String pathSpec();
    public abstract  JsonElement doPost(HttpServletRequest req) throws IOException;
    public abstract String getJsonBody(HttpServletRequest req) throws IOException;

}
