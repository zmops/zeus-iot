package com.zmops.iot.web.sys.controller;

import com.google.code.kaptcha.Constants;
import com.zmops.iot.constant.ConstantsContext;
import com.zmops.iot.core.web.base.BaseController;
import com.zmops.iot.model.exception.InvalidKaptchaException;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.model.response.SuccessResponseData;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.auth.AuthService;
import com.zmops.iot.web.sys.dto.param.LoginParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * @author nantian created at 2021/7/29 19:55
 * <p>
 * 登陆
 */
@RestController
public class LoginController extends BaseController {

    @Autowired
    private AuthService authService;

    /**
     * 登录返回用户信息
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "/login")
    public ResponseData loginVali(@Valid @RequestBody LoginParam vo) {

        String username = vo.getUsername();
        String password = vo.getPassword();


        if (ConstantsContext.getKaptchaOpen()) {
            String kaptcha = super.getPara("kaptcha").trim();
            String code    = (String) super.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
            if (ToolUtil.isEmpty(kaptcha) || !kaptcha.equalsIgnoreCase(code)) {
                throw new InvalidKaptchaException();
            }
        }

        //登录并返回登录用户信息
        return new SuccessResponseData(authService.login(username, password));
    }

    /**
     * 退出登录
     */
    @RequestMapping(value = "/logout")
    @ResponseBody
    public ResponseData logOut() {
        authService.logout();
        return new SuccessResponseData();
    }

    @RequestMapping("/getCharts")
    public void getCookie(HttpServletResponse response,
                          @RequestParam("from") String from,
                          @RequestParam("to") String to,
                          @RequestParam("attrIds") List<Long> attrIds,
                          @RequestParam("width") String width,
                          @RequestParam("height") String height) {
        authService.getCharts(response,from,to,attrIds,width,height);
    }
}
