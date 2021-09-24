package com.zmops.iot.core.auth.filter;


import com.zmops.iot.core.auth.cache.SessionManager;
import com.zmops.iot.core.auth.jwt.JwtTokenUtil;
import com.zmops.iot.core.auth.util.TokenUtil;
import com.zmops.iot.util.ToolUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.zmops.iot.constant.ConstantsContext.getTokenHeaderName;

/**
 * 这个过滤器，在所有请求之前，也在spring security filters之前
 * <p>
 * 这个过滤器的作用是：接口在进业务之前，添加登录上下文（SecurityContext和LoginContext）
 * <p>
 * 没有用session，只能token来校验当前的登录人的身份，所以在进业务之前要给当前登录人设置登录状态
 *
 * @author fengshuonan
 */
@Component
public class JwtAuthorizationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private SessionManager sessionManager;

    public JwtAuthorizationTokenFilter() {
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        // 1.静态资源直接过滤，不走此过滤器
        for (String reg : NoneAuthedResources.FRONTEND_RESOURCES) {
            if (new AntPathMatcher().match(reg, request.getServletPath())) {
                chain.doFilter(request, response);
                return;
            }
        }

        // 2.从cookie和header获取token
        String authToken = TokenUtil.getToken();

        // 3.通过token获取用户名
        String username = null;
        if (ToolUtil.isNotEmpty(authToken)) {
            try {
                username = JwtTokenUtil.getJwtPayLoad(authToken).getAccount();
            } catch (IllegalArgumentException | JwtException e) {
                //请求token为空或者token不正确，忽略，并不是所有接口都要鉴权
            }
        }

        // 4.如果账号不为空，并且没有设置security上下文
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 5.从缓存中拿userDetails，如果不为空，就设置登录上下文和权限上下文
            UserDetails userDetails = sessionManager.getSession(authToken);
            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                chain.doFilter(request, response);
                return;
            } else {

                // 6.当用户的token过期了，缓存中没有用户信息，则删除相关cookies
                Cookie[] tempCookies = request.getCookies();
                if (tempCookies != null) {
                    for (Cookie cookie : tempCookies) {
                        if (getTokenHeaderName().equals(cookie.getName())) {
                            Cookie temp = new Cookie(cookie.getName(), "");
                            temp.setMaxAge(0);
                            temp.setPath("/");
                            response.addCookie(temp);
                        }
                    }
                }

                //如果是不需要权限校验的接口不需要返回session超时
                for (String reg : NoneAuthedResources.BACKEND_RESOURCES) {
                    if (new AntPathMatcher().match(reg, request.getServletPath())) {
                        chain.doFilter(request, response);
                        return;
                    }
                }

                //跳转到登录超时
                response.setHeader("Zeus-Session-Timeout", "true");
                request.getRequestDispatcher("/global/sessionError").forward(request, response);
            }
        }

        chain.doFilter(request, response);
    }
}
