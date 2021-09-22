package com.zmops.iot.web.nats;

import io.nats.client.Connection;
import io.nats.client.Nats;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * * @author yefei
 * <p>
 * 初始化 连接
 **/
@Component
public class NatsConnectionInitializer implements InitializingBean {
    @Value("${nats.url}")
    private String url;


    @Override
    public void afterPropertiesSet() throws Exception {
        Connection nc = Nats.connect(url);
        NatsConnectionHolder.INSTANCE.setConnection(new NatsConnectionImpl(nc));
    }
}
