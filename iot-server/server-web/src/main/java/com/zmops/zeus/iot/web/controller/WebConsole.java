package com.zmops.zeus.iot.web.controller;

import com.zmops.zeus.server.library.web.core.ActionKey;
import com.zmops.zeus.server.library.web.core.Controller;
import com.zmops.zeus.server.library.web.core.Path;

/**
 * @author nantian created at 2021/12/1 22:10
 */
@Path("/")
public class WebConsole extends Controller {

    public void index() {
        redirect("/zeus/index.html");
    }


    @ActionKey("/zeus")
    public void zeus() {
        redirect("/zeus/index.html");
    }
}
