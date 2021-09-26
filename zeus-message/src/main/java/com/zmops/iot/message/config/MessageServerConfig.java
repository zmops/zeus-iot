package com.zmops.iot.message.config;

import cn.hutool.core.util.StrUtil;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author nantian created at 2021/9/26 22:39
 */

@Configuration
@EnableConfigurationProperties({SocketIoConfig.class})
public class MessageServerConfig {

    @Bean
    public SocketIOServer server(SocketIoConfig socketIoConfig) {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();

        config.setHostname(socketIoConfig.getHost());
        config.setPort(socketIoConfig.getPort());

        config.setAuthorizationListener(data -> {
            String token = data.getSingleUrlParam("token");
            return StrUtil.isNotBlank(token);
        });

        return new SocketIOServer(config);
    }


    /**
     * Spring 扫描自定义注解
     */
    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer server) {
        return new SpringAnnotationScanner(server);
    }

}
