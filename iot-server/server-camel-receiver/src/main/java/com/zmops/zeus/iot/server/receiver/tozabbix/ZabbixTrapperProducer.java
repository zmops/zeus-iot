package com.zmops.zeus.iot.server.receiver.tozabbix;

import com.google.gson.Gson;

import com.zmops.zeus.iot.server.library.module.ModuleManager;
import com.zmops.zeus.iot.server.receiver.tozabbix.worker.ItemDataTransferWorker;
import com.zmops.zeus.iot.server.util.StringUtil;
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

    private final ModuleManager          moduleManager;
    private final ItemDataTransferWorker itemDataTransferWorker;
    private final ExecutorService        itemValueThread = Executors.newFixedThreadPool(20);


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
        List<ItemValue> values = (List<ItemValue>) message.getBody(); //TODO 类型检查

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
