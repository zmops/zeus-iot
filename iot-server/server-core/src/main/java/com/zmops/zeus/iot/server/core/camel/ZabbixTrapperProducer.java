package com.zmops.zeus.iot.server.core.camel;

import com.zmops.zeus.iot.server.core.worker.ItemDataTransferWorker;
import com.zmops.zeus.iot.server.core.worker.data.ItemValue;
import com.zmops.zeus.iot.server.library.module.ModuleManager;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultProducer;

import java.util.List;

/**
 * @author nantian created at 2021/8/16 22:48
 * <p>
 * to("Zabbix")
 */
public class ZabbixTrapperProducer extends DefaultProducer {

    private final ModuleManager          moduleManager;
    private final ItemDataTransferWorker itemDataTransferWorker;


    public ZabbixTrapperProducer(Endpoint endpoint, ModuleManager moduleManager) {
        super(endpoint);
        this.moduleManager = moduleManager;
        this.itemDataTransferWorker = new ItemDataTransferWorker(moduleManager);
    }

    /**
     * Body 必须是 ItemValue
     *
     * @param exchange Exchange
     */
    @Override
    public void process(Exchange exchange) {
        Message         message = exchange.getIn();
        List<ItemValue> values  = (List<ItemValue>) message.getBody();

        values.forEach(itemDataTransferWorker::in);
        exchange.getMessage().setBody("{\"success\":\"true\"}");
    }
}
