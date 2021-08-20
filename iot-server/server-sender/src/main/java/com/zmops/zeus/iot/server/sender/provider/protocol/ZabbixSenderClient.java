package com.zmops.zeus.iot.server.sender.provider.protocol;

import com.zmops.zeus.iot.server.sender.provider.ZabbixSenderModuleConfig;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author nantian created at 2021/8/14 14:46
 */

@Slf4j
public class ZabbixSenderClient {

    private static ZabbixSenderModuleConfig socketConfig;

    public ZabbixSenderClient(ZabbixSenderModuleConfig config) {
        socketConfig = config;
    }

    /**
     * 启动 TCP Sender 客户端
     */
    public void start() {
        log.debug("Zabbix Sender 模块已经启动，Trapper 服务地址：{}:{}",
                socketConfig.getHost(), socketConfig.getPort());
    }


    /**
     * 获取 Socket 实例
     *
     * @return Socket
     */
    public static Socket getSocket() {
        Socket trapperSocket = new Socket();
        try {
            trapperSocket.setSoTimeout(1000);
            trapperSocket.connect(new InetSocketAddress(socketConfig.getHost(), socketConfig.getPort()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return trapperSocket;
    }
}
