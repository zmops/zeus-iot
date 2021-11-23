package com.zmops.zeus.iot.web.controller;

import com.zmops.zeus.server.library.web.core.Controller;
import com.zmops.zeus.server.library.web.core.Path;

/**
 * @author nantian created at 2021/11/23 23:24
 */

@Path(value = "/123", viewPath = "/")
public class IndexController extends Controller {

    public void hello() {
        renderJson("hello", "world");
    }
}
