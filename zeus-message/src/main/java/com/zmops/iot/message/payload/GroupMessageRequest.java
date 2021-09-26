package com.zmops.iot.message.payload;

import lombok.Data;

/**
 * @author nantian created at 2021/9/26 23:16
 */
@Data
public class GroupMessageRequest {
    /**
     * 消息发送方用户id
     */
    private String fromUid;

    /**
     * 群组id
     */
    private String groupId;

    /**
     * 消息内容
     */
    private String message;
}
