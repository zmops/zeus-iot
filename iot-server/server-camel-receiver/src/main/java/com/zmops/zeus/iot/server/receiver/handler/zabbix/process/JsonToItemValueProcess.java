package com.zmops.zeus.iot.server.receiver.handler.zabbix.process;

import com.google.gson.Gson;
import com.zmops.zeus.dto.ItemValue;
import com.zmops.zeus.iot.server.receiver.handler.zabbix.IoTDeviceValue;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nantian created at 2021/8/23 17:16
 * <p>
 * Json 转 对象，进队列，一定要转成 ItemValue 对象，{@link ItemValue}
 */
public class JsonToItemValueProcess implements Processor {

    private final Gson gson = new Gson();

    @Override
    public void process(Exchange exchange) throws Exception {
        Message message = exchange.getIn();
        InputStream bodyStream = (InputStream) message.getBody();
        String inputContext = this.analysisMessage(bodyStream);


        IoTDeviceValue iotValue = gson.fromJson(inputContext, IoTDeviceValue.class);

        List<ItemValue> itemValueList = new ArrayList<>();

        iotValue.getAttributes().forEach((key, value) -> {
            ItemValue item = new ItemValue(iotValue.getDeviceId(), iotValue.getClock());
            item.setKey(key);
            item.setValue(value);

            itemValueList.add(item);
        });

        exchange.getMessage().setBody(itemValueList);
    }


    private String analysisMessage(InputStream bodyStream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] contextBytes = new byte[4096];
        int realLen;
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
