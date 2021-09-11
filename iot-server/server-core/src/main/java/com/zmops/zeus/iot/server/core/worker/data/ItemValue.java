package com.zmops.zeus.iot.server.core.worker.data;

import afu.org.checkerframework.checker.igj.qual.I;
import com.zmops.zeus.iot.server.core.camel.IOTDeviceValue;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nantian created at 2021/8/23 15:15
 */

@Getter
@Setter
public class ItemValue implements Item {

    private String host;  // 【设备ID】

    private String key; // 【属性标识】

    // 【设备上报 值】，都是文本
    // Zabbix 会根据配置的ITEM 类型，进行转换，如果失败就报错
    private String value;

    private Long clock; // 毫秒，如果为 Null，则 zabbix 以接收时间为准

    private Long ns; // 纳秒，如果为 Null，则 zabbix 以接收时间为准


    public ItemValue(List<ItemValue> itemValueList) {
        this.valueList = itemValueList;
    }

    public ItemValue() {
        super();
    }

    private List<ItemValue> valueList;

    public void addItemValue(IOTDeviceValue deviceValue) {

        ItemValue itemValue = new ItemValue();

        itemValue.setHost(deviceValue.getDeviceId());
        itemValue.setKey(deviceValue.getDeviceAttrKey());
        itemValue.setValue(deviceValue.getDeviceAttrValue());

        if (deviceValue.getDeviceTime() != null) {
            itemValue.setClock(deviceValue.getDeviceTime());
        }

        this.valueList.add(itemValue);
    }


    /**
     * 设置 数据时间，单独设置 以设备推送的时间数据为准
     *
     * @param clock 毫秒，70年到现在
     * @param ns    纳秒，0-9位数
     */
    public void setTime(Long clock, Long ns) {
        this.clock = clock;
        this.ns = ns;
    }


    @Override
    public String host() {
        return getHost();
    }

    @Override
    public String key() {
        return getKey();
    }
}
