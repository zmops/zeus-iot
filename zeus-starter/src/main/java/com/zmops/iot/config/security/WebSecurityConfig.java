package com.zmops.iot.config.security;

import com.zmops.iot.core.auth.entrypoint.JwtAuthenticationEntryPoint;
import com.zmops.iot.core.auth.filter.JwtAuthorizationTokenFilter;
import com.zmops.iot.core.auth.filter.NoneAuthedResources;
import com.zmops.iot.web.sys.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

/**
 * @author nantian created at 2021/7/29 22:54
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtAuthorizationTokenFilter authenticationTokenFilter;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailsService);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        //csrf关闭
        httpSecurity.csrf().disable();

        //开启跨域
        httpSecurity.cors();

        //自定义退出
        httpSecurity.logout().disable();

        //禁用匿名用户
        httpSecurity.anonymous().disable();

        httpSecurity.exceptionHandling().authenticationEntryPoint(unauthorizedHandler);

        // 全局不创建session
        httpSecurity.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //放开一些接口的权限校验
        for (String notAuthedResource : NoneAuthedResources.BACKEND_RESOURCES) {
            httpSecurity.authorizeRequests().antMatchers(notAuthedResource).permitAll();
        }

        //其他接口都需要权限
        httpSecurity.authorizeRequests().anyRequest().authenticated();

        //添加自定义的过滤器
        httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        //disable page caching
        httpSecurity.headers().frameOptions().sameOrigin().cacheControl();

    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.POST, NoneAuthedResources.NO_AUTH_API)

                // 静态资源放开过滤
                .and().ignoring().antMatchers(HttpMethod.GET,
                "/assets/**",
                "/favicon.ico",
                "/activiti-editor/**","/websocket/alarm/**"
        );

    }
}
