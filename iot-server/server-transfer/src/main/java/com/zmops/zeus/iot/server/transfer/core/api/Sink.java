package com.zmops.zeus.iot.server.transfer.core.api;

/**
 * Sink data to remote data center
 */
public interface Sink extends Stage {

    /**
     * Write data into data center
     *
     * @param message - message
     */
    void write(Message message);


    /**
     * set source file name where the message is generated
     *
     * @param sourceFileName
     */
    void setSourceFile(String sourceFileName);
}
