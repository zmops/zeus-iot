package com.zmops.iot.web.analyse.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.util.LocalDateTimeUtils;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.analyse.dto.LatestDto;
import com.zmops.iot.web.analyse.enums.CpuLoadEnum;
import com.zmops.iot.web.analyse.enums.MemoryUtilizationEnum;
import com.zmops.iot.web.analyse.enums.ProcessEnum;
import com.zmops.iot.web.product.dto.ZbxTriggerInfo;
import com.zmops.zeus.driver.entity.ZbxItemInfo;
import com.zmops.zeus.driver.service.ZbxHistoryGet;
import com.zmops.zeus.driver.service.ZbxHost;
import com.zmops.zeus.driver.service.ZbxItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yefei
 **/
@Service
public class SelfMonitorService {

    @Autowired
    ZbxHistoryGet zbxHistoryGet;

    @Autowired
    ZbxHost zbxHost;

    @Autowired
    ZbxItem zbxItem;

    @Autowired
    HistoryService historyService;

    private static Map<String, String> itemMap = new HashMap<>();

    private static Map<String, String> hostIdMap = new HashMap<>();


    public Map<String, Object> getMemInfo() {
        Map<String, Object> resMap = new HashMap<>(3);

        getItemValue(getHostId("Zabbix server"), MemoryUtilizationEnum.utilization.getCode(), MemoryUtilizationEnum.utilization.getMessage(), 0, resMap);
        getItemValue(getHostId("Zabbix server"), MemoryUtilizationEnum.total.getCode(), MemoryUtilizationEnum.total.getMessage(), 3, resMap);
        getItemValue(getHostId("Zabbix server"), MemoryUtilizationEnum.available.getCode(), MemoryUtilizationEnum.available.getMessage(), 3, resMap);

        resMap.put("trends", getTrendsData(getHostId("Zabbix server"), itemMap.get(MemoryUtilizationEnum.utilization.getMessage()), 0));
        return resMap;
    }

    public Map<String, Object> getCpuLoadInfo() {
        Map<String, Object> resMap = new HashMap<>(3);

        getItemValue(getHostId("Zabbix server"), CpuLoadEnum.avg1.getCode(), CpuLoadEnum.avg1.getMessage(), 0, resMap);
        getItemValue(getHostId("Zabbix server"), CpuLoadEnum.avg5.getCode(), CpuLoadEnum.avg5.getMessage(), 0, resMap);
        getItemValue(getHostId("Zabbix server"), CpuLoadEnum.avg15.getCode(), CpuLoadEnum.avg15.getMessage(), 0, resMap);

        resMap.put("trends", getTrendsData(getHostId("Zabbix server"), itemMap.get(CpuLoadEnum.avg1.getMessage()), 0));
        return resMap;
    }

    public Map<String, Object> getProcessInfo() {
        Map<String, Object> resMap = new HashMap<>(3);

        getItemValue(getHostId("Zabbix server"), ProcessEnum.num.getCode(), ProcessEnum.num.getMessage(), 3, resMap);
        getItemValue(getHostId("Zabbix server"), ProcessEnum.run.getCode(), ProcessEnum.run.getMessage(), 3, resMap);
        getItemValue(getHostId("Zabbix server"), ProcessEnum.max.getCode(), ProcessEnum.max.getMessage(), 3, resMap);

        resMap.put("trends", getTrendsData(getHostId("Zabbix server"), itemMap.get(ProcessEnum.run.getMessage()), 3));
        return resMap;
    }


    public List<Map<String, String>> getCpuUtilization() {
        String itemId = getItemId(getHostId("Zabbix server"), "CpuUtilization", "system.cpu.util");
        if (ToolUtil.isEmpty(itemId)) {
            return Collections.emptyList();
        }
        return getTrendsData(getHostId("Zabbix server"), itemId, 0);
    }


    /**
     * 获取趋势数据
     *
     * @param hostId        主机ID
     * @param itemId        监控项ID
     * @param itemValueType 监控项值类型
     * @return
     */
    private List<Map<String, String>> getTrendsData(String hostId, String itemId, int itemValueType) {
        long timeTill = LocalDateTimeUtils.getSecondsByTime(LocalDateTime.now());
        long timeFrom = LocalDateTimeUtils.getSecondsByTime(LocalDateTimeUtils.minu(LocalDateTime.now(), 1L, ChronoUnit.HOURS));

        List<LatestDto> latestDtos = historyService.queryHitoryData(hostId, Collections.singletonList(itemId), 20, itemValueType, timeFrom, timeTill);

        return latestDtos.stream().map(o -> {
            Map<String, String> tmpMap = new HashMap<>(2);
            tmpMap.put("date", LocalDateTimeUtils.convertTimeToString(Integer.parseInt(o.getClock()), "yyyy-MM-dd HH:mm:ss"));
            tmpMap.put("val", o.getValue());
            return tmpMap;
        }).collect(Collectors.toList());
    }

    /**
     * 获取监控项最新值
     *
     * @param hostId        主机ID
     * @param key           监控项key
     * @param name          监控项名称
     * @param itemValueType 监控项值类型
     * @param resMap        map
     */
    private void getItemValue(String hostId, String key, String name, int itemValueType, Map<String, Object> resMap) {
        String itemId = getItemId(hostId, name, key);

        if (ToolUtil.isEmpty(itemId)) {
            return;
        }
        String res = zbxHistoryGet.historyGet(null, Collections.singletonList(itemId), 1, itemValueType, null, null);
        List<LatestDto> latestDtos = JSONObject.parseArray(res, LatestDto.class);

        if (ToolUtil.isNotEmpty(latestDtos)) {
            resMap.put(name, latestDtos.get(0).getValue());
        }
    }

    /**
     * 从缓存取 hostId
     *
     * @param host 主机名称
     * @return hostId
     */
    private String getHostId(String host) {
        String hostId = hostIdMap.get(host);
        if (ToolUtil.isEmpty(hostId)) {
            hostId = getHostIdByName(host);
            hostIdMap.put(host, hostId);
        }
        return hostId;
    }

    /**
     * 从Zbx取 hostId
     *
     * @param host 主机名称
     * @return hostId
     */
    private String getHostIdByName(String host) {
        String s = zbxHost.hostGet(host);
        List<ZbxTriggerInfo.Host> hosts = JSON.parseArray(s, ZbxTriggerInfo.Host.class);
        if (ToolUtil.isNotEmpty(hosts)) {
            return hosts.get(0).getHostid();
        }
        return "";
    }

    /**
     * 从缓存取 itemId
     *
     * @param hostId 主机ID
     * @param name   监控项名称
     * @param key    监控项key
     * @return itemId
     */
    private String getItemId(String hostId, String name, String key) {
        String itemId = itemMap.get(name);
        if (ToolUtil.isEmpty(itemId)) {
            itemId = getItemIdByName(hostId, key);
            itemMap.put(name, itemId);
        }
        return itemId;
    }

    /**
     * 从Zbx取 itemId
     *
     * @param hostId 主机ID
     * @param key    监控项key
     * @return itemId
     */
    private String getItemIdByName(String hostId, String key) {
        if (ToolUtil.isEmpty(key) || ToolUtil.isEmpty(hostId)) {
            return "";
        }

        String item = zbxItem.getItemList(key, hostId);
        List<ZbxItemInfo> itemInfos = JSON.parseArray(item, ZbxItemInfo.class);
        if (ToolUtil.isNotEmpty(itemInfos)) {
            return itemInfos.get(0).getItemid();
        }
        return "";
    }


}
