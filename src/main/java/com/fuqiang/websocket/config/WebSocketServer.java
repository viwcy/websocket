package com.fuqiang.websocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * websocket服务类
 *
 * @author fuqiang
 * @date 2019/4/17 19:11
 * <p>
 * ServerEndpoint不支持注入，切记
 */
@ServerEndpoint("/websocket/{userid}")
@Component
@Slf4j
public class WebSocketServer {
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。相当于多个windows客户端
     */
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) throws IOException {
        this.session = session;
        //加入set中
        webSocketSet.add(this);
        addOnlineCount();
        log.info("【Websocket】 有新的客户端连接，sessionId为{}，当前在线数:{}", session.getId(), getOnlineCount());
        /*
         *一定是单发，通知刚链接的windows用户
         */
        sendMessage("Websocket链接成功", session.getId());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        //从set中删除
        webSocketSet.remove(this);
        subOnlineCount();
        log.info("【Websocket】 有一连接关闭，当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("【Websocket】 收到客户端发来的消息: {}", message);
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("【Websocket】 链接发生错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送(单发送)
     */
    public static String sendMessage(String message, String sessionId) throws IOException {
        Session session = null;
        for (WebSocketServer webSocket : webSocketSet) {
            if (webSocket.session.getId().equals(sessionId)) {
                session = webSocket.session;
            }
        }
        if (session != null) {
            session.getBasicRemote().sendText(message);
            log.info("【Websocket】 向客户端单发送消息：{}", message);
            return "消息发送成功";
        } else {
            log.info("【Websocket】 未找到该客户端会话，或此会话已关闭");
            return "消息发送失败，未找到此客户端";
        }
    }

    /**
     * 实现服务器主动推送(群发自定义消息)
     */
    public static void sendMessages(String message) {
        log.info("【Websocket】 向客户端广播消息：{}", message);
        for (WebSocketServer webSocket : webSocketSet) {
            try {
                webSocket.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public static CopyOnWriteArraySet<WebSocketServer> getWebSocketSet() {
        return webSocketSet;
    }
}
