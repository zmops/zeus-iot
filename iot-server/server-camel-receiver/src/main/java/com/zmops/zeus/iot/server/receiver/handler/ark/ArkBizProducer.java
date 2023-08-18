package com.zmops.zeus.iot.server.receiver.handler.ark;

import com.zmops.zeus.dto.DataMessage;
import com.zmops.zeus.dto.ItemValue;
import com.zmops.zeus.facade.DynamicProcotol;
import com.zmops.zeus.iot.server.receiver.module.CamelReceiverModule;
import com.zmops.zeus.iot.server.receiver.service.ReferenceClientService;
import com.zmops.zeus.server.library.module.ModuleManager;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;

import java.util.Collections;
import java.util.List;

/**
 * @author nantian created at 2021/12/2 22:35
 */
@SuppressWarnings("all")
public class ArkBizProducer extends DefaultProducer {

    private final ModuleManager moduleManager;

    private final DynamicProcotol dynamicProcotol;

    public ArkBizProducer(Endpoint endpoint, ModuleManager moduleManager, String uniqueId, String methodName) {
        super(endpoint);
        this.moduleManager = moduleManager;

        ReferenceClientService referenceClientService = moduleManager.find(CamelReceiverModule.NAME)
                .provider().getService(ReferenceClientService.class);
        dynamicProcotol = referenceClientService.getDynamicProtocolClient(uniqueId);
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        DataMessage message = new DataMessage();
        message.setBody(exchange.getMessage().getBody());
        message.setHeaders(exchange.getMessage().getHeaders());

        List<ItemValue> itemValueList = dynamicProcotol.protocolHandler(message);

        if (itemValueList == null) {
            itemValueList = Collections.emptyList();
        }

        exchange.getIn().setBody(itemValueList);
    }
}
