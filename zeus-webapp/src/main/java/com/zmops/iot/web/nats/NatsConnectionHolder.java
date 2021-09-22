package com.zmops.iot.web.nats;

/**
 * @author yefei
 **/
public enum NatsConnectionHolder {

    INSTANCE;

    private NatsConnection connection;

    public NatsConnection getConnection() {
        return connection;
    }

    public void setConnection(NatsConnection connection) {
        this.connection = connection;
    }

}
