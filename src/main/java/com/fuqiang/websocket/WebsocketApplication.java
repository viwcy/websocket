package com.fuqiang.websocket;

import com.fuqiang.websocket.config.WebSocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Date;

@SpringBootApplication
public class WebsocketApplication {

    public static void main(String[] args) {
//        SpringApplication.run(WebsocketApplication.class, args);

        SpringApplication springApplication = new SpringApplication(WebsocketApplication.class);
        ConfigurableApplicationContext configurableApplicationContext = springApplication.run(args);
        //解决WebSocket不能注入的问题
        WebSocketServer.setApplicationContext(configurableApplicationContext);
    }

}
