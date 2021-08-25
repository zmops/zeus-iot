package com.zmops.iot.web.alarm.socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.util.ToolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author yefei
 */

@ServerEndpoint("/websocket/alarm/{userid}")
@Component
@Slf4j
public class WebSocketServer {

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int                                        onlineCount  = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private        Session                                    session;
    /**
     * 接收token
     */
    private        String                                     token        = "";
    /**
     * userid
     */
    private        String                                     userid       = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(@PathParam("userid") String userid, Session session) {
        Map<String, List<String>> map = session.getRequestParameterMap();
        this.session = session;
        this.userid = userid;
        if (webSocketMap.containsKey(userid)) {
            webSocketMap.remove(userid);
            //加入map中
            webSocketMap.put(userid, this);
        } else {
            //加入map中
            webSocketMap.put(userid, this);
            //在线数加1
            addOnlineCount();
        }

        log.info("用户连接:" + userid + ",当前在线人数为:" + getOnlineCount());

        try {
            sendMessage("连接成功");
        } catch (IOException e) {
            log.error("用户:" + userid + ",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(userid)) {
            //从map中删除
            webSocketMap.remove(userid);
            subOnlineCount();
        }
        log.info("用户退出:" + userid + ",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户消息:" + userid + ",报文:" + message);
        //消息保存到数据库、redis
        if (ToolUtil.isNotEmpty(message)) {
            try {
                //解析发送的报文
                JSONObject jsonObject = JSON.parseObject(message);
                //追加发送人(防止串改)
                jsonObject.put("fromUserId", this.token);
                String toUserId = jsonObject.getString("toUserId");
                //传送给对应toUserId用户的websocket
                if (ToolUtil.isNotEmpty(toUserId) && webSocketMap.containsKey(toUserId)) {
                    webSocketMap.get(toUserId).sendMessage(jsonObject.toJSONString());
                } else {
                    log.error("请求的userId:" + toUserId + "不在该服务器上");
                    //否则不在这个服务器上，发送到mysql或者redis
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:" + this.userid + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 实现服务器主动推送给固定人
     */
    public static void sendMessageTo(String message, String to) {
        try {
            if (webSocketMap != null && webSocketMap.get(to) != null) {
                webSocketMap.get(to).session.getBasicRemote().sendText(message);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 群发消息
     *
     * @param message
     */
    public static void sendMessageToAll(String message) {
        if (webSocketMap != null) {
            webSocketMap.values().forEach(e -> {
                try {
                    e.sendMessage(message);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }


    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
