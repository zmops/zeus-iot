package com.zmops.iot.core.auth.context;


import com.zmops.iot.util.SpringContextHolder;

/**
 * 当前登录用户信息获取的接口
 *
 * @author fengshuonan
 */
public class LoginContextHolder {

    public static LoginContext getContext() {
        return SpringContextHolder.getBean(LoginContext.class);
    }

}
