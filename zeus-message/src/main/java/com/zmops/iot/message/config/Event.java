package com.zmops.iot.message.config;

/**
 * @author nantian created at 2021/9/26 23:15
 */
public interface Event {

    /**
     * 聊天事件
     */
    String CHAT = "chat";

    /**
     * 广播消息
     */
    String BROADCAST = "broadcast";

    /**
     * 群聊
     */
    String GROUP = "group";

    /**
     * 加入群聊
     */
    String JOIN = "join";
}
