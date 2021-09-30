package com.zmops.iot.message.server;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @author nantian created at 2021/9/26 23:21
 */

@Slf4j
@Component
public class SocketIoServer implements CommandLineRunner {

    @Autowired
    private SocketIOServer server;

    @Override
    public void run(String... args) throws Exception {
        server.start();
    }

    /**
     * 退出时 销毁 SocketIOServer
     *
     * @throws Exception
     */
    @PreDestroy
    public void stop() throws Exception {
        server.stop();
    }
}

