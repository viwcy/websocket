package com.fuqiang.websocket.controller;

import com.fuqiang.websocket.config.WebSocketServer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * <p>Title: MessageApi</p>
 * <p>Description: MessageApi</p>
 * <p>Copyright: Xi An BestTop Technologies, ltd. Copyright(c) 2018/p>
 *
 * @author Fuqiang
 * @version 0.0.0.1
 * <pre>Histroy:
 *       2019/12/27 0027 14:53 Create by Fuqiang
 * </pre>
 */
@RestController
@RequestMapping("/websocket")
public class MessageApi {

    @PostMapping("/sends")
    public String sends(@RequestParam String msg) {
        WebSocketServer.sendMessages(msg);
        return "ok";
    }

    @PostMapping("/send")
    public String send(@RequestParam String msg, @RequestParam String sessionId) {
        try {
            return WebSocketServer.sendMessage(msg, sessionId);
        } catch (IOException e) {
            e.printStackTrace();
            return "error...";
        }
    }
}
