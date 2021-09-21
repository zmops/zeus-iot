package com.zmops.iot.web.exception.page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 全局的控制器
 *
 * @author fengshuonan
 */
@Controller
@RequestMapping("/global")
public class GlobalController {

    /**
     * 跳转到404页面
     */
    @RequestMapping(path = "/error")
    public String errorPage() {
        return "/404";
    }

    /**
     * 跳转到session超时页面
     */
    @RequestMapping(path = "/sessionError")
    public String errorPageInfo(Model model) {
        return "/index";
    }
}
