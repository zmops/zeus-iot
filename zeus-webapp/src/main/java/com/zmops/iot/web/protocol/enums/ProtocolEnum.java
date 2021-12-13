package com.zmops.iot.web.protocol.enums;

import lombok.Getter;

/**
 * @author yefei
 **/
public enum ProtocolEnum {
    HttpServer("5","HttpServer"),
    MqttClient("1","MqttClient"),
    TcpServer("2","TcpServer"),
    UdpServer("4","UdpServer"),
    CoapServer("9","CoapServer"),
    WebSocketServer("7","WebSocketServer");

    @Getter
    String code;
    @Getter
    String message;

    ProtocolEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getDescription(String status) {
        if (status == null) {
            return "";
        } else {
            for (ProtocolEnum s : ProtocolEnum.values()) {
                if (s.getCode().equals(status)) {
                    return s.getMessage();
                }
            }
            return "";
        }
    }
}
