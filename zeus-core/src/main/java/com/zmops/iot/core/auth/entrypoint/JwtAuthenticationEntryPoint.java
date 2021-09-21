package com.zmops.iot.core.auth.entrypoint;


import com.alibaba.fastjson.JSON;
import com.zmops.iot.core.auth.exception.enums.AuthExceptionEnum;
import com.zmops.iot.model.response.ErrorResponseData;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * 这个端点用在用户访问受保护资源但是不提供任何token的情况下
 * <p>
 * 当前用户没有登录（没有token），访问了系统中的一些需要权限的接口，就会进入这个处理器
 *
 * @author fengshuonan
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -1L;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // GET请求跳转到主页
        if ("get".equalsIgnoreCase(request.getMethod())
                && !request.getHeader("Accept").contains("application/json")) {

            response.sendRedirect(request.getContextPath() + "/global/sessionError");

        } else {
            // POST请求返回json
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json");

            ErrorResponseData errorResponseData = new ErrorResponseData(
                    AuthExceptionEnum.NO_PAGE_ERROR.getCode(), AuthExceptionEnum.NO_PAGE_ERROR.getMessage());

            response.getWriter().write(JSON.toJSONString(errorResponseData));
        }
    }
}