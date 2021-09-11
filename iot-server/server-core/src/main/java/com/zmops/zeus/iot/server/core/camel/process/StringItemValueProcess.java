package com.zmops.zeus.iot.server.core.camel.process;

import com.google.gson.Gson;
import com.zmops.zeus.iot.server.core.camel.IOTDeviceValue;
import com.zmops.zeus.iot.server.core.worker.data.ItemValue;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nantian created at 2021/8/23 17:16
 * <p>
 * Json 转 对象，进队列，一定要转成 ItemValue 对象，{@link ItemValue}
 */
public class StringItemValueProcess implements Processor {

    private final Gson gson = new Gson();

    @Override
    public void process(Exchange exchange) throws Exception {
        Message message = exchange.getIn();

        IOTDeviceValue iotValue = gson.fromJson((String) message.getBody(), IOTDeviceValue.class);

        List<ItemValue> itemValueList = new ArrayList<>();

        iotValue.getAttributes().forEach((key, value) -> {
            ItemValue item = new ItemValue(iotValue.getDeviceId(), iotValue.getClock());
            item.setKey(key);
            item.setValue(value);

            itemValueList.add(item);
        });

        exchange.getMessage().setBody(itemValueList);
    }
}
