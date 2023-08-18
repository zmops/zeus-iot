package com.zmops.zeus.iot.server.starter;

import com.alipay.sofa.ark.support.startup.SofaArkBootstrap;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nantian created at 2021/8/13 15:26
 * <p>
 * 宙斯服务 协议层启动
 */
@Slf4j
public class IoTServerStartUp {

    public static void main(String[] args) {
        SofaArkBootstrap.launch(args);
        IoTServerBootstrap.start();

        log.info("IoT Server Platform start successfully ...");
    }
}
