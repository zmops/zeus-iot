package com.zmops.iot.message.controller;

import com.zmops.iot.message.handler.MessageEventHandler;
import com.zmops.iot.message.payload.BroadcastMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
