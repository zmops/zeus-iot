package com.zmops.zeus.iot.server.receiver.tozabbix;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nantian created at 2021/8/23 16:49
 */
public class ZabbixTrapper {


    @Getter
    private final String request = "sender data";

    @Setter
    @Getter
    private List<ItemValue> data;

    public ZabbixTrapper(List<ItemValue> itemValues) {
        data = new ArrayList<>();
        data.addAll(itemValues);
    }
}
