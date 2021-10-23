package com.zmops.zeus.iot.server.receiver.tozabbix;

import lombok.Getter;
import lombok.Setter;

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

    private Long clock; // 秒，如果为 Null，则 zabbix 以接收时间为准

    private Long ns; // 纳秒，如果为 Null，则 zabbix 以接收时间为准


    public ItemValue(String host, Long clock) {
        this.host = host;
        if (clock != null) {
            this.clock = clock;
        }
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
