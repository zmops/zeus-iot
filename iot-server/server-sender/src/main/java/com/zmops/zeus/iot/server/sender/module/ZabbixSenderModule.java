package com.zmops.zeus.iot.server.sender.module;

import com.zmops.zeus.iot.server.library.module.ModuleDefine;
import com.zmops.zeus.iot.server.sender.service.ZabbixSenderService;

/**
 * @author nantian created at 2021/8/14 14:35
 * <p>
 * 数据发送到 Zabbix Trapper，参数值："sender data"
 */
public class ZabbixSenderModule extends ModuleDefine {

    public static final String NAME = "zabbix-sender";


    public ZabbixSenderModule() {
        super(NAME);
    }

    @Override
    public Class[] services() {
        return new Class[]{
                ZabbixSenderService.class
        };
    }
}
