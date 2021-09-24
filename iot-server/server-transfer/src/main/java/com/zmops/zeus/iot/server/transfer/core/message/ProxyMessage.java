package com.zmops.zeus.iot.server.transfer.core.message;

import com.zmops.zeus.iot.server.transfer.api.Message;

import java.util.Map;

/**
 * Bus message with body, header, bid and tid.
 */
public class ProxyMessage implements Message {

    private static final String DEFAULT_TID = "__";

    private final byte[]              body;
    private final Map<String, String> header;
    private final String              bid;
    private final String              tid;


    public ProxyMessage(byte[] body, Map<String, String> header) {
        this.body = body;
        this.header = header;
        this.bid = header.get("bid");
        this.tid = header.getOrDefault("tid", DEFAULT_TID);
    }

    /**
     * Get first line of body list
     *
     * @return first line of body list
     */
    @Override
    public byte[] getBody() {
        return body;
    }

    /**
     * Get header of message
     *
     * @return header
     */
    @Override
    public Map<String, String> getHeader() {
        return header;
    }

    public String getBid() {
        return bid;
    }

    public String getTid() {
        return tid;
    }

    public static ProxyMessage parse(Message message) {
        return new ProxyMessage(message.getBody(), message.getHeader());
    }
}
