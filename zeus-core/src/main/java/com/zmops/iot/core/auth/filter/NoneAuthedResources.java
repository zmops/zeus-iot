package com.zmops.iot.core.auth.filter;

/**
 * 不需要身份验证的资源
 *
 * @author fengshuonan
 */
public class NoneAuthedResources {

    /**
     * 前端接口资源
     */
    public static final String[] FRONTEND_RESOURCES = {
            "/assets/**",
            "/favicon.ico",
            "/activiti-editor/**"
    };


    /**
     * 不走 filter 调用链 API
     */
    public static final String[] NO_AUTH_API = {
            "/login",
            "/device/status",
            "/device/problem"
    };


    /**
     * 不要权限校验的后端接口资源
     * <p>
     * ANT风格的接口正则表达式：
     * <p>
     * ? 匹配任何单字符<br/>
     * * 匹配0或者任意数量的 字符<br/>
     * ** 匹配0或者更多的 目录<br/>
     */
    public static final String[] BACKEND_RESOURCES = {
            //主页
            "/",

            //获取验证码
            "/kaptcha",

            //rest方式获取token入口
            "/rest/login",
            // 登录接口放开过滤
            "/login",
            "/hello",

            //oauth登录的接口
            "/oauth/render/*",
            "/oauth/callback/*",

            //单点登录接口
            "/ssoLogin",
            "/sysTokenLogin",

            // session登录失效之后的跳转
            "/global/sessionError",

            // 图片预览 头像
            "/system/preview/*",

            // 错误页面的接口
            "/error",
            "/global/error"
    };

}
