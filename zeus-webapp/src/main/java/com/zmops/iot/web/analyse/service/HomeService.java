package com.zmops.iot.web.analyse.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.query.QProduct;
import com.zmops.iot.util.LocalDateTimeUtils;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.alarm.dto.param.AlarmParam;
import com.zmops.iot.web.alarm.service.AlarmService;
import com.zmops.iot.web.analyse.dto.LatestDto;
import com.zmops.zeus.driver.entity.ZbxItemInfo;
import com.zmops.zeus.driver.entity.ZbxProblemInfo;
import com.zmops.zeus.driver.service.ZbxHost;
import com.zmops.zeus.driver.service.ZbxItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 全局概览服务
 **/
@Service
public class HomeService {

    @Autowired
    private ZbxHost zbxHost;

    @Autowired
    private ZbxItem zbxItem;

    @Autowired
    HistoryService historyService;

    @Autowired
    AlarmService alarmService;

    private static String hostId = "";

    private static Map<String, String> ITEM_Map = new ConcurrentHashMap<>(5);

    //Zbx 指标取数速率 key
    private static final String KEY = "zabbix[wcache,values";

    /**
     * 服务器取数速率统计
     *
     * @return
     */
    public Map<String, List<LatestDto>> collectonRate() {
        if (ToolUtil.isEmpty(hostId)) {
            if (ToolUtil.isEmpty(getZbxServerId())) {
                return new HashMap<>(0);
            }
        }
        if (ToolUtil.isEmpty(ITEM_Map)) {
            if (ToolUtil.isEmpty(getItemMap())) {
                return new HashMap<>(0);
            }
        }
        List<String> itemIds = new ArrayList<>(ITEM_Map.keySet());
        List<LatestDto> latestDtos = historyService.queryHitoryData(hostId, itemIds, 0, LocalDateTimeUtils.getSecondsByTime(LocalDateTimeUtils.minu(LocalDateTime.now(),
                7, ChronoUnit.DAYS)), LocalDateTimeUtils.getSecondsByTime(LocalDateTime.now()));

        latestDtos.forEach(latestDto -> {
            latestDto.setClock(LocalDateTimeUtils.convertTimeToString(Integer.parseInt(latestDto.getClock()), "yyyy-MM-dd HH:mm:ss"));
            if (null != ITEM_Map.get(latestDto.getItemid())) {
                latestDto.setName(ITEM_Map.get(latestDto.getItemid()));
            }
        });

        return latestDtos.parallelStream().collect(Collectors.groupingBy(LatestDto::getName));
    }

    /**
     * 获取服务器 hostid
     *
     * @return
     */
    private String getZbxServerId() {
        String                    response = zbxHost.hostGet("Zabbix server");
        List<Map<String, String>> ids      = JSON.parseObject(response, List.class);
        if (null != ids && ids.size() > 0) {
            hostId = ids.get(0).get("hostid");
            return hostId;
        }
        return "";
    }

    /**
     * 获取服务器 取数的ITEM
     *
     * @return
     */
    private Map<String, String> getItemMap() {
        String            itemList  = zbxItem.getItemList(KEY, hostId);
        List<ZbxItemInfo> itemInfos = JSONObject.parseArray(itemList, ZbxItemInfo.class);
        for (ZbxItemInfo itemInfo : itemInfos) {
            ITEM_Map.put(itemInfo.getItemid(), formatName(itemInfo.getName()));
        }
        return ITEM_Map;
    }

    /**
     * 格式化显示的名称
     */
    private static String formatName(String name) {
        if (name.length() < 53) {
            return "avg";
        }
        return name.substring(35, name.length() - 18);
    }


    /**
     * 统计设备数量
     *
     * @return
     */
    public Map<String, Integer> getDeviceNum() {
        Map<String, Integer> deviceNumMap = new HashMap<>(4);

        deviceNumMap.put("online", 0);

        List<Device> list = new QDevice().findList();
        deviceNumMap.put("total", list.size());
        deviceNumMap.put("disable", (int) list.parallelStream().filter(o -> "DISABLE".equals(o.getStatus())).count());
        deviceNumMap.put("product", new QProduct().findCount());


        return deviceNumMap;
    }

    /**
     * 告警数量统计
     *
     * @return
     */
    private static final String[] severity = {"", "信息", "低级", "中级", "高级", "紧急"};

    public Map<String, Object> getAlarmNum() {
        AlarmParam alarmParam = new AlarmParam();
        //7天前的日期
        alarmParam.setClock(LocalDateTimeUtils.getSecondsByTime(LocalDateTimeUtils.minu(LocalDateTime.now(), 7, ChronoUnit.DAYS)));
        List<ZbxProblemInfo> alarmList = alarmService.getAlarmList(alarmParam);
        Map<String, Object>  alarmMap  = new HashMap<>(6);
        if (ToolUtil.isEmpty(alarmList)) {
            return alarmMap;
        }

        Map<String, Map<String, Long>> tmpMap = alarmList.parallelStream().filter(o -> o.getR_clock().equals("0") && !o.getSeverity().equals("0")).collect(
                Collectors.groupingBy(ZbxProblemInfo::getSeverity,
                        Collectors.groupingBy(ZbxProblemInfo::getClock, Collectors.counting()
                        )
                )
        );

        tmpMap.forEach((key, value) -> {
            List list = new ArrayList();
            value.forEach((date, val) -> {
                Map<String, Object> valMap = new HashMap<>(2);
                valMap.put("date", date);
                valMap.put("val", val);
                list.add(valMap);
            });
            alarmMap.put(severity[Integer.parseInt(key)], list);
        });

        //今日开始时间
        Long timeStart     = LocalDateTimeUtils.getSecondsByTime(LocalDateTimeUtils.getDayStart(LocalDateTime.now()));
        Long todayAlarmNum = alarmList.parallelStream().filter(o -> Long.parseLong(o.getClock()) >= timeStart).collect(Collectors.counting());
        alarmMap.put("today", todayAlarmNum);
        return alarmMap;
    }
}
