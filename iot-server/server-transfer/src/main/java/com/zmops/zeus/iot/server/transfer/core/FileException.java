package com.zmops.zeus.iot.server.transfer.core;

public class FileException extends RuntimeException {

    public FileException(String message, Throwable ex) {
        super(message, ex);
    }
}
