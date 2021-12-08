package com.zmops.zeus.iot.server.receiver.handler.zabbix;

import com.google.gson.Gson;
import com.zmops.zeus.dto.ItemValue;
import com.zmops.zeus.iot.server.receiver.handler.zabbix.worker.ItemDataTransferWorker;
import com.zmops.zeus.server.library.module.ModuleManager;
import com.zmops.zeus.server.library.util.StringUtil;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultProducer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author nantian created at 2021/8/16 22:48
 * <p>
 * to("Zabbix")
 */
public class ZabbixTrapperProducer extends DefaultProducer {

    private final ModuleManager moduleManager;
    private final ItemDataTransferWorker itemDataTransferWorker;
    private final ExecutorService itemValueThread = Executors.newFixedThreadPool(20);


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
        Message message = exchange.getIn();

        if (message.getBody() == null && !(message.getBody() instanceof List)) {
            return;
        }

        List<ItemValue> values = (List<ItemValue>) message.getBody();

        for (ItemValue itemValue : values) {
            if (StringUtil.isEmpty(itemValue.getHost())
                    || StringUtil.isEmpty(itemValue.getKey())
                    || StringUtil.isEmpty(itemValue.getValue())) {
                log.error(" process item data error，{}", new Gson().toJson(itemValue));
                continue;
            }

            itemValueThread.submit(() -> {
                itemDataTransferWorker.in(itemValue);
            });
        }

        exchange.getMessage().setBody("{\"success\":\"true\"}");
    }
}
