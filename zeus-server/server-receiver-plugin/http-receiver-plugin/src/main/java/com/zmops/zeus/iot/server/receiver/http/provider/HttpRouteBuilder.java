package com.zmops.zeus.iot.server.receiver.http.provider;

import com.zmops.zeus.iot.server.library.module.ModuleManager;
import com.zmops.zeus.iot.server.receiver.http.predicate.HeaderPredicate;
import com.zmops.zeus.iot.server.sender.module.ZabbixSenderModule;
import com.zmops.zeus.iot.server.sender.provider.service.ZabbixSenderService;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author nantian created at 2021/8/14 22:41
 */
public class HttpRouteBuilder extends RouteBuilder {

    private final HttpReceiverConfig config;
    private final ModuleManager      moduleManager;

    public HttpRouteBuilder(HttpReceiverConfig config, ModuleManager moduleManager) {
        this.config = config;
        this.moduleManager = moduleManager;
    }

    @Override
    public void configure() throws Exception {
        fromF("netty4-http:http://0.0.0.0:%d/foo", config.getPort())
                .threads(10)
                .choice()
                .when(new HeaderPredicate())
                .process(new HttpProcessor())

                .to("log:helloworld?showExchangeId=true");
    }


    public class HttpProcessor implements Processor {

        @Override
        public void process(Exchange exchange) throws Exception {
            Message     message      = exchange.getIn();
            InputStream bodyStream   = (InputStream) message.getBody();
            String      inputContext = this.analysisMessage(bodyStream);

            ZabbixSenderService senderService = moduleManager.find(ZabbixSenderModule.NAME).provider().getService(ZabbixSenderService.class);

            String resp = senderService.sendData(inputContext);

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


}
