package com.zmops.zeus.iot.server.core.camel;

import com.zmops.zeus.iot.server.library.module.ModuleManager;
import com.zmops.zeus.iot.server.sender.module.ZabbixSenderModule;
import com.zmops.zeus.iot.server.sender.provider.service.ZabbixSenderService;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultProducer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author nantian created at 2021/8/16 22:48
 * <p>
 * to("Zabbix")
 */
public class ZabbixTrapperProducer extends DefaultProducer {

    private final ModuleManager moduleManager;


    public ZabbixTrapperProducer(Endpoint endpoint, ModuleManager moduleManager) {
        super(endpoint);
        this.moduleManager = moduleManager;
    }

    /**
     * TODO 数据写入 队列，进来之前 是 JSON 格式
     *
     * @param exchange Exchange
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        Message     message      = exchange.getIn();
        InputStream bodyStream   = (InputStream) message.getBody();
        String      inputContext = this.analysisMessage(bodyStream);

        ZabbixSenderService senderService = moduleManager.find(ZabbixSenderModule.NAME).provider().getService(ZabbixSenderService.class);

        String resp = senderService.sendData(inputContext);

        // 把Zabbix 响应结果放入 Message 返回
        message.setBody(resp);
    }


    private String analysisMessage(InputStream bodyStream) throws IOException {
        ByteArrayOutputStream outStream    = new ByteArrayOutputStream();
        byte[]                contextBytes = new byte[4096];
        int                   realLen;
        while ((realLen = bodyStream.read(contextBytes, 0, 4096)) != -1) {
            outStream.write(contextBytes, 0, realLen);
        }

        // 返回从Stream中读取的字串
        try {
            return outStream.toString("UTF-8");
        } finally {
            outStream.close();
        }
    }
}
