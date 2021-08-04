package com.zmops.zeus.driver.entity;

import lombok.Data;

/**
 * @author nantian created at 2021/8/2 16:59
 */

@Data
public class ZbxResponseData {

    private String jsonrpc;

    private String result;

    private ErrorInfo error;


    @Data
    public static class ErrorInfo {

        private Integer code;

        private String message;

        private String data;
    }

}
