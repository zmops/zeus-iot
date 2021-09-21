package com.zmops.iot.web.sys.controller;

import com.google.code.kaptcha.Constants;
import com.zmops.iot.constant.ConstantsContext;
import com.zmops.iot.core.auth.context.LoginContextHolder;
import com.zmops.iot.core.web.base.BaseController;
import com.zmops.iot.domain.sys.SysUser;
import com.zmops.iot.model.exception.InvalidKaptchaException;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.model.response.SuccessResponseData;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.auth.AuthService;
import com.zmops.iot.web.sys.dto.param.LoginParam;
import com.zmops.iot.web.sys.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author nantian created at 2021/7/29 19:55
 * <p>
 * 登陆
 */
@Controller
public class LoginController extends BaseController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SysUserService userService;

    /**
     * 跳转到主页
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {

        //判断用户是否登录
        if (LoginContextHolder.getContext().hasLogin()) {
            Map<String, Object> userIndexInfo = userService.getUserIndexInfo();

            //用户信息为空，提示账号没分配角色登录不进去
            if (userIndexInfo == null) {
                model.addAttribute("tips", "该用户没有角色，无法登陆");
                return "/login.html";
            } else {
                model.addAllAttributes(userIndexInfo);
                return "/index.html";
            }

        } else {
            return "/index";
        }
    }


    /**
     * 跳转到登录页面
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        if (LoginContextHolder.getContext().hasLogin()) {
            return REDIRECT + "/";
        } else {
            return "/index";
        }
    }

    /**
     * 登录返回用户信息
     *
     * @param loginParam 登陆参数
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseData loginVali(@Valid @RequestBody LoginParam loginParam) {
        String username = loginParam.getUsername();
        String password = loginParam.getPassword();

        if (ConstantsContext.getKaptchaOpen()) {
            String kaptcha = super.getPara("kaptcha").trim();
            String code    = (String) super.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
            if (ToolUtil.isEmpty(kaptcha) || !kaptcha.equalsIgnoreCase(code)) {
                throw new InvalidKaptchaException();
            }
        }

        return new SuccessResponseData(authService.login(username, password));
    }

    /**
     * 退出登录
     */
    @ResponseBody
    @RequestMapping(value = "/logout")
    public ResponseData logOut() {
        authService.logout();
        return new SuccessResponseData();
    }

}
