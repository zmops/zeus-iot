package com.zmops.iot.web.nats;

import io.nats.client.Connection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;

/**
 * @author yefei
 **/
@Slf4j
public class NatsConnectionImpl implements NatsConnection {

    private Connection connection;

    public NatsConnectionImpl(Connection connection) {
        Assert.state(connection != null, "connection could not be null");
        this.connection = connection;
    }

    @Override
    public void publish(String topic, String messageBody) {
        connection.publish(topic, messageBody.getBytes(StandardCharsets.UTF_8));
    }
}
