package com.fuqiang.websocket.controller;

import com.fuqiang.websocket.config.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>Title: TestController</p>
 * <p>Description: TestController</p>
 * <p>Copyright: Xi An BestTop Technologies, ltd. Copyright(c) 2018/p>
 *
 * @author Fuqiang
 * @version 0.0.0.1
 * <pre>Histroy:
 *       2019/12/26 0026 10:32 Create by Fuqiang
 * </pre>
 */
@Controller
@RequestMapping("/websocket")
public class TestController {

    @Autowired
    private WebSocketServer webSocketServer;

    @GetMapping("/index/{id}")
    public String test(@PathVariable String id, Model model) {
        System.out.println(id);
        model.addAttribute("id", id);
        return "index";
    }
}
