package com.zmops.iot.config.security;

import com.zmops.iot.web.auth.PermissionAop;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author nantian created at 2021/7/30 18:28
 */
@Configuration
public class PermissionAopConfig {

    /**
     * 资源过滤的aop
     *
     * @author fengshuonan
     */
    @Bean
    public PermissionAop permissionAop() {
        return new PermissionAop();
    }

}
