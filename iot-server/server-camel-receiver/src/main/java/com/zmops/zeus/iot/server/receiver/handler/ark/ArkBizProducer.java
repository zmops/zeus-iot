package com.zmops.zeus.iot.server.receiver.handler.ark;

import com.zmops.zeus.dto.DataMessage;
import com.zmops.zeus.dto.ItemValue;
import com.zmops.zeus.facade.DynamicProcotol;
import com.zmops.zeus.server.library.module.ModuleManager;
import com.zmops.zeus.server.runtime.SofaStarter;
import com.zmops.zeus.server.runtime.api.client.ReferenceClient;
import com.zmops.zeus.server.runtime.api.client.param.ReferenceParam;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

import java.util.Collections;
import java.util.List;

/**
 * @author nantian created at 2021/12/2 22:35
 */
@SuppressWarnings("all")
public class ArkBizProducer extends DefaultProducer {

    private final SofaStarter sofaStarter;
    private final ReferenceClient referenceClient;

    private final String methodName;
    private final ModuleManager moduleManager;

    private final DynamicProcotol dynamicClient;

    public ArkBizProducer(Endpoint endpoint, ModuleManager moduleManager, String uniqueId, String methodName) {
        super(endpoint);
        this.moduleManager = moduleManager;
        this.sofaStarter = new SofaStarter();
        this.referenceClient = this.sofaStarter.getSofaRuntimeContext().getClientFactory().getClient(ReferenceClient.class);
        this.methodName = methodName;

        ReferenceParam<DynamicProcotol> referenceParam = new ReferenceParam<>();
        referenceParam.setInterfaceType(DynamicProcotol.class);
        referenceParam.setUniqueId(uniqueId);
        dynamicClient = referenceClient.reference(referenceParam);
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        DataMessage message = new DataMessage();
        message.setBody(exchange.getMessage().getBody());
        message.setHeaders(exchange.getMessage().getHeaders());

        List<ItemValue> itemValueList = dynamicClient.protocolHandler(message);

        if (itemValueList == null) {
            itemValueList = Collections.emptyList();
        }

        exchange.getIn().setBody(itemValueList);
    }
}
