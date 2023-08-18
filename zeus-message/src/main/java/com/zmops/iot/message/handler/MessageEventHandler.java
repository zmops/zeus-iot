package com.zmops.iot.message.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.zmops.iot.message.config.Event;
import com.zmops.iot.message.config.UserClientMap;
import com.zmops.iot.message.payload.BroadcastMessageRequest;
import com.zmops.iot.message.payload.GroupMessageRequest;
import com.zmops.iot.message.payload.JoinRequest;
import com.zmops.iot.message.payload.SingleMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

/**
 * @author nantian created at 2021/9/26 23:13
 */

@Slf4j
@Component
public class MessageEventHandler {

    @Autowired
    private SocketIOServer server;

    @Autowired
    private UserClientMap userClient;


    @OnConnect
    public void onConnect(SocketIOClient client) {
        if (client != null) {

            String token = client.getHandshakeData().getSingleUrlParam("token");
            String userId = client.getHandshakeData().getSingleUrlParam("userId");

            UUID sessionId = client.getSessionId();

            userClient.save(userId, sessionId);

            log.info("socketio connect successfully,【token】= {},【sessionId】= {}", token, sessionId);
        }
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        if (client != null) {
            String userId = client.getHandshakeData().getSingleUrlParam("token");
            userClient.deleteByUserId(userId);
            client.disconnect();
        }
    }

    @OnEvent(value = Event.JOIN)
    public void onJoinEvent(SocketIOClient client, AckRequest request, JoinRequest data) {

        log.info("user：{} has join group chat：{}", data.getUserId(), data.getGroupId());
        client.joinRoom(data.getGroupId());

        server.getRoomOperations(data.getGroupId()).sendEvent(Event.JOIN, data);
    }


    @OnEvent(value = Event.CHAT)
    public void onChatEvent(SocketIOClient client, AckRequest request, SingleMessageRequest data) {
        System.out.println(data.getMessage());
    }

    @OnEvent(value = Event.GROUP)
    public void onGroupEvent(SocketIOClient client, AckRequest request, GroupMessageRequest data) {
        Collection<SocketIOClient> clients = server.getRoomOperations(data.getGroupId()).getClients();
    }

    public void sendDisconnectMsg(String userId) {
        if (userClient.findByUserId(userId).isPresent()) {
            UUID uuid = userClient.findByUserId(userId).get();
            if (null != server.getClient(uuid)) {
                BroadcastMessageRequest message = new BroadcastMessageRequest();
                message.setMessage("disconnect");
                server.getClient(uuid).sendEvent(Event.BROADCAST, message);
            }
        }
    }

    public void sendToUser(String userId, String msg) {
        if (userClient.findByUserId(userId).isPresent()) {
            UUID uuid = userClient.findByUserId(userId).get();
            if (null != server.getClient(uuid)) {
                BroadcastMessageRequest message = new BroadcastMessageRequest();
                message.setMessage(msg);
                server.getClient(uuid).sendEvent(Event.BROADCAST, message);
            }
        }
    }

    public void sendToBroadcast(BroadcastMessageRequest message) {
        for (UUID clientId : userClient.findAll()) {
            if (server.getClient(clientId) == null) {
                continue;
            }

            server.getClient(clientId).sendEvent(Event.BROADCAST, message);
        }
    }

    public void sendToGroup(GroupMessageRequest message) {
        server.getRoomOperations(message.getGroupId()).sendEvent(Event.GROUP, message);
    }
}
