package com.zmops.zeus.iot.server.core.camel.process;

import com.google.gson.Gson;
import com.zmops.zeus.iot.server.core.worker.data.ItemValue;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author nantian created at 2021/8/23 17:16
 * <p>
 * Json 转 对象，进队列，一定要转成 ItemValue 对象，{@link ItemValue}
 */
public class ByteArrayToItemValueProcess implements Processor {

    private final Gson gson = new Gson();

    @Override
    public void process(Exchange exchange) throws Exception {
        Message message = exchange.getIn();
        byte[] messageBytes = (byte[]) message.getBody();

        String inputContext = new String(messageBytes, StandardCharsets.UTF_8);

        Map<String, String> jsonMap = gson.fromJson(inputContext, Map.class);
        String key = jsonMap.get("key");
        String value = jsonMap.get("value");
        if (StringUtils.isBlank(key)) {
            return;
        }
        String deviceId = key.substring(0, key.indexOf("."));
        String attributeKey = key.substring(key.indexOf(".") + 1);
        List<ItemValue> itemValueList = new ArrayList<>();

        ItemValue item = new ItemValue(deviceId, System.currentTimeMillis() / 1000);
        item.setKey(attributeKey);
        item.setValue(value);

        itemValueList.add(item);

        exchange.getMessage().setBody(itemValueList);
    }
}
