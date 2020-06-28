package com.fuqiang.websocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * <p>Title: WebSocketServer</p>
 * <p>Description: WebSocketServer</p>
 * <p>
 * TODO  @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * TODO  注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 *
 * @author Fuqiang
 * @version 0.0.1
 */
@ServerEndpoint("/websocket/{remark}")
@Component
@Slf4j
public class WebSocketServer {
    /**
     * 记录当前在线连接数。
     */
    private static volatile int onlineCount = 0;
    /**
     * 存放每个客户端对应的webSocket对象。相当于多个windows客户端
     */
    private static volatile CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    private String remark;

    /**
     * 应用上下文，用来获取spring bean
     */
    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketServer.applicationContext = applicationContext;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(@PathParam(value = "remark") String remark, Session session) throws IOException {
        System.out.println(1/0);
        this.session = session;
        this.remark = remark;
        webSocketSet.add(this);
        addOnlineCount();
        log.info("【Websocket】 链接成功，sessionId: {}，当前链接数: {}", session.getId(), getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        subOnlineCount();
        log.info("【Websocket】 有一连接关闭，sessionId: {}，当前链接数: {}", this.session.getId(), getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("【Websocket】 收到客户端发来的消息：{}", message);
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
    public static String sendMessage(String message, String sessionId) {
        Session session = null;
        for (WebSocketServer webSocket : webSocketSet) {
            if (webSocket.session.getId().equals(sessionId)) {
                session = webSocket.session;
            }
        }
        if (session != null) {
            try {
                session.getBasicRemote().sendText(message);
                log.info("【Websocket】 向客户端单发送消息成功，消息体：{}，sessionId: {}", message, session.getId());
            } catch (IOException e) {
                e.printStackTrace();
                log.info("【Websocket】 向客户端单发送消息失败，消息体: {}，原因: {}", message, e.getCause());
            }
            return "消息发送成功";
        } else {
            log.info("【Websocket】 未找到该客户端会话，或此会话已关闭");
            return "消息发送失败，未找到此客户端";
        }
    }

    public void sendMessage(String message, Session session) {
        try {
            session.getBasicRemote().sendText(message);
            log.info("【Websocket】 向客户端单发送消息成功，消息体：{}，sessionId: {}", message, session.getId());
        } catch (IOException e) {
            e.printStackTrace();
            log.info("【Websocket】 向客户端单发送消息失败，消息体: {}，原因: {}", message, e.getCause());
        }
    }

    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
            log.info("【Websocket】 向客户端单发送消息成功，消息体：{}，sessionId: {}", message, session.getId());
        } catch (IOException e) {
            e.printStackTrace();
            log.info("【Websocket】 向客户端单发送消息失败，消息体: {}，原因: {}", message, e.getCause());
        }
    }

    /**
     * 实现服务器主动推送(群发自定义消息)
     */
    public static void sendMessages(String message) throws Exception {
        log.info("【Websocket】 向客户端广播消息：{}", message);
        for (WebSocketServer webSocket : webSocketSet) {
            webSocket.session.getBasicRemote().sendText(message);
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

    public Session getSession() {
        return session;
    }

    public String getRemark() {
        return remark;
    }
}
