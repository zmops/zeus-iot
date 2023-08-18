package com.zmops.iot.message.payload;

import lombok.Data;

/**
 * @author nantian created at 2021/9/26 23:16
 */
@Data
public class JoinRequest {
    /**
     * 用户id
     */
    private String userId;

    /**
     * 群名称
     */
    private String groupId;
}
