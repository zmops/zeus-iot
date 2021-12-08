package com.zmops.iot.message.controller;

import com.zmops.iot.message.handler.MessageEventHandler;
import com.zmops.iot.message.payload.BroadcastMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author nantian created at 2021/9/27 0:16
 */

@Slf4j
@RestController
@RequestMapping("/send")
public class SocketIoController {

    @Autowired
    private MessageEventHandler messageHandler;

    /**
     * just for test
     *
     * @param message
     */
    @PostMapping("/broadcast")
    public void broadcast(@RequestBody BroadcastMessageRequest message) {
        messageHandler.sendToBroadcast(message);
    }

    @PostMapping("/sendToUser")
    public void sendToUser(@RequestParam("userId") String userId,@RequestParam("msg") String msg) {
        messageHandler.sendToUser(userId,msg);
    }
}
