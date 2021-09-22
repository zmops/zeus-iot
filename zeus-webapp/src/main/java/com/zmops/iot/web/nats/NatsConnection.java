package com.zmops.iot.web.nats;

/**
 * @author yefei
 **/
public interface NatsConnection {
   void publish(String topic, String messageBody);
}
