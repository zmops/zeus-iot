package com.zmops.zeus.iot.server.transfer.core.message;

import com.zmops.zeus.iot.server.transfer.api.Message;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DefaultMessage implements Message {

    private final byte[]              body;
    private final Map<String, String> header;

    public DefaultMessage(byte[] body, Map<String, String> header) {
        this.body = body;
        this.header = header;
    }

    public DefaultMessage(byte[] body) {
        this(body, new HashMap<>());
    }

    @Override
    public byte[] getBody() {
        return body;
    }

    @Override
    public Map<String, String> getHeader() {
        return header;
    }

    @Override
    public String toString() {
        return new String(body, StandardCharsets.UTF_8);
    }
}
