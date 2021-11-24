package com.zmops.iot.web.analyse.service;

import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.analyse.enums.CpuLoadEnum;
import com.zmops.zeus.driver.service.ZbxHistoryGet;
import com.zmops.zeus.driver.service.ZbxHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yefei
 **/
@Service
public class SelfMonitorService {

    @Autowired
    ZbxHistoryGet zbxHistoryGet;

    @Autowired
    ZbxHost zbxHost;

    private static Map<String, List<String>> itemMap = new HashMap<>();

    private static Map<String, String> hostIdMap = new HashMap<>();

    public Map<String, String> getMemInfo() {
        return null;
    }

    public Map<String, String> getCpuInfo() {
        List<String> itemIds = itemMap.get("cpu");
        if (ToolUtil.isEmpty(itemIds)) {
            itemIds = getItemIdByNames(Arrays.asList(CpuLoadEnum.avg1.getCode(), CpuLoadEnum.avg5.getCode(), CpuLoadEnum.avg15.getCode()));
            itemMap.put("cpu", itemIds);
        }
        String hostId = hostIdMap.get("Zabbix server");
        if (ToolUtil.isEmpty(hostId)) {
            hostId = getHostIdByName("Zabbix server");
        }
        String res = zbxHistoryGet.historyGet(hostId, itemIds, itemIds.size(), 0, null, null);
        return null;
    }

    private String getHostIdByName(String host) {
        String s = zbxHost.hostGet(host);
        return "";
    }

    public String getProcessInfo() {
        return "";
    }

    private List<String> getItemIdByNames(List<String> names) {
        return null;
    }


}
