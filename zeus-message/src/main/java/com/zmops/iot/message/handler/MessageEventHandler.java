package com.zmops.iot.message.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;

import com.zmops.iot.message.config.Event;
import com.zmops.iot.message.payload.BroadcastMessageRequest;
import com.zmops.iot.message.payload.GroupMessageRequest;
import com.zmops.iot.message.payload.JoinRequest;
import com.zmops.iot.message.payload.SingleMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author nantian created at 2021/9/26 23:13
 */

@Slf4j
@Component
public class MessageEventHandler {

    @Autowired
    private SocketIOServer server;

    /**
     * 添加connect事件，当客户端发起连接时调用
     *
     * @param client 客户端对象
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        if (client != null) {

            String token = client.getHandshakeData().getSingleUrlParam("token");

            // 模拟用户id 和token一致
            String userId = client.getHandshakeData().getSingleUrlParam("token");

        } else {
            log.error("客户端为空");
        }
    }

    /**
     * 添加disconnect事件，客户端断开连接时调用，刷新客户端信息
     *
     * @param client 客户端对象
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        if (client != null) {
            String token = client.getHandshakeData().getSingleUrlParam("token");

            // 模拟用户id 和token一致
            String userId = client.getHandshakeData().getSingleUrlParam("token");

            client.disconnect();
        } else {
            log.error("客户端为空");
        }
    }

    /**
     * 加入群聊
     *
     * @param client  客户端
     * @param request 请求
     * @param data    群聊
     */
    @OnEvent(value = Event.JOIN)
    public void onJoinEvent(SocketIOClient client, AckRequest request, JoinRequest data) {

        log.info("用户：{} 已加入群聊：{}", data.getUserId(), data.getGroupId());
        client.joinRoom(data.getGroupId());

        server.getRoomOperations(data.getGroupId()).sendEvent(Event.JOIN, data);
    }


    @OnEvent(value = Event.CHAT)
    public void onChatEvent(SocketIOClient client, AckRequest request, SingleMessageRequest data) {

    }

    @OnEvent(value = Event.GROUP)
    public void onGroupEvent(SocketIOClient client, AckRequest request, GroupMessageRequest data) {
        Collection<SocketIOClient> clients = server.getRoomOperations(data.getGroupId()).getClients();
    }


    /**
     * 广播
     */
    public void sendToBroadcast(BroadcastMessageRequest message) {
        log.info("系统紧急广播一条通知：{}", message.getMessage());


    }

    /**
     * 群聊
     */
    public void sendToGroup(GroupMessageRequest message) {
        server.getRoomOperations(message.getGroupId()).sendEvent(Event.GROUP, message);
    }
}
