package com.zmops.iot.message.payload;

import lombok.Data;

/**
 * @author nantian created at 2021/9/26 23:16
 */
@Data
public class BroadcastMessageRequest {

    /**
     * 消息内容
     */
    private String message;
}
