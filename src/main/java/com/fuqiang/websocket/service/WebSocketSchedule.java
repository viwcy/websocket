package com.fuqiang.websocket.service;

import com.fuqiang.websocket.config.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * <p>Title: WebSocketSchedule</p>
 * <p>Description: WebSocketSchedule</p>
 *
 * @author Fuqiang
 * @version 0.0.0.1
 */
@Component
@EnableScheduling
@Slf4j
public class WebSocketSchedule {

    private static final String WEBSOCKET_REMARK = "telematics";

    @Scheduled(cron = "*/10 * * * * ?")
    public void WebSocketTask() {
        CopyOnWriteArraySet<WebSocketServer> webSocketSet = WebSocketServer.getWebSocketSet();
        for (WebSocketServer webSocketServer : webSocketSet) {
            String remark = webSocketServer.getRemark();
            Session session = webSocketServer.getSession();
            log.info("当前remark: {}，当前sessionId: {}", remark, session.getId());
            webSocketServer.sendMessage(remark, session);
        }
    }
}
