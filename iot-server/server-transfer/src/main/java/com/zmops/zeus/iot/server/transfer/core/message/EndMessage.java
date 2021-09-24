package com.zmops.zeus.iot.server.transfer.core.message;

import com.zmops.zeus.iot.server.transfer.core.api.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * End message, this is an empty message only indicate
 * that source data have been completely consumed.
 */
public class EndMessage implements Message {

    @Override
    public byte[] getBody() {
        return null;
    }

    @Override
    public Map<String, String> getHeader() {
        return new HashMap<>(10);
    }
}
