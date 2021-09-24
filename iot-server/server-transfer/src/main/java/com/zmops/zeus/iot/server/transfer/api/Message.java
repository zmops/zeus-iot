package com.zmops.zeus.iot.server.transfer.api;

import java.util.Map;

/**
 * Message used in inner-data transfer, message is divided into
 * two parts, header and body. header is the attributes of message
 * and body is the content of message.
 */
public interface Message {

    /**
     * Data content of message.
     *
     * @return bytes body
     */
    byte[] getBody();

    /**
     * Data attribute of message
     *
     * @return map header
     */
    Map<String, String> getHeader();
}
