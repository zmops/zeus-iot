package com.zmops.zeus.iot.server.sender.service;

import com.google.gson.Gson;
import com.zmops.zeus.iot.server.sender.provider.ZabbixSenderClient;
import com.zmops.zeus.server.library.module.ModuleManager;
import com.zmops.zeus.server.library.module.Service;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nantian created at 2021/8/14 18:39
 */

@Slf4j
public class ZabbixSenderService implements Service {

    private final ModuleManager moduleManager;

    private static final Gson gson = new Gson();

    public ZabbixSenderService(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    /**
     * 指定格式的 JSON
     *
     * @param message see
     *                https://www.zabbix.com/documentation/current/manual/appendix/items/trapper
     *
     * {
     * 	"request":"sender data",
     * 	"data":[
     *      {
     *          "host":"device.info",
     *          "key":"device.temp",
     * 	        "value":"86"
     *      }
     * 	]
     * }
     * @return String
     * @throws IOException ex
     */
    public String sendData(String message) throws IOException {

        Socket trapperSocket = ZabbixSenderClient.getSocket();

        int payloadLength = length(message);
        byte[] header = new byte[]{
                'Z', 'B', 'X', 'D', '\1',
                (byte) (payloadLength & 0xFF),
                (byte) ((payloadLength >> 8) & 0xFF),
                (byte) ((payloadLength >> 16) & 0xFF),
                (byte) ((payloadLength >> 24) & 0xFF),
                '\0', '\0', '\0', '\0'};

        ByteBuffer byteBuffer = ByteBuffer.allocate(header.length + payloadLength);
        byteBuffer.put(header);
        byteBuffer.put(message.getBytes(StandardCharsets.UTF_8));

        byte[] response = new byte[2048];

        OutputStream reqStream = trapperSocket.getOutputStream();
        reqStream.write(byteBuffer.array());
        reqStream.flush();

        byteBuffer = null;

        InputStream resStream = trapperSocket.getInputStream();

        StringBuilder resp = new StringBuilder();

        int headLength = 13;
        int bRead = 0;

        while (true) {
            bRead = resStream.read(response);
            if (bRead <= 0) break;
            resp.append(new String(Arrays.copyOfRange(response, headLength, bRead)));
            headLength = 0;
        }

        log.debug(" Zabbix Trapper 响应数据：{} ", resp.toString());

        resStream.close();
        reqStream.close();
        trapperSocket.close();
        trapperSocket = null;

        return zabbixResponseToMap(resp.toString());
    }


    /**
     * zabbix 返回字符串 处理
     *
     * @param resp
     * @return String
     */
    private String zabbixResponseToMap(String resp) {
        Map<String, String> result = gson.fromJson(resp, Map.class);

        String info = result.get("info");
        if (info == null) {
            return resp;
        }

        String[] infos = info.split(";");

        Map<String, String> resultMap = new HashMap<>();
        for (String i : infos) {
            String[] ii = i.split(":");
            resultMap.put(ii[0].trim(), ii[1]);
        }

        resultMap.put("response", result.get("response"));
        return gson.toJson(resultMap);
    }

    /**
     * 计算字符串长度 中文3个字节
     * @param value
     * @return
     */
    public static int length(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 3;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }
}
