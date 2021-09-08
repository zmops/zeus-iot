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
import java.util.*;
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
    public List<Map<String, Object>> collectonRate(long timeFrom, long timeTill) {
        if (ToolUtil.isEmpty(hostId)) {
            if (ToolUtil.isEmpty(getZbxServerId())) {
                return Collections.emptyList();
            }
        }
        if (ToolUtil.isEmpty(ITEM_Map)) {
            if (ToolUtil.isEmpty(getItemMap())) {
                return Collections.emptyList();
            }
        }
        List<String>    itemIds    = new ArrayList<>(ITEM_Map.keySet());
        List<LatestDto> latestDtos = historyService.queryHitoryData(hostId, itemIds, 0, timeFrom, timeTill);

        latestDtos.forEach(latestDto -> {
//            latestDto.setClock(LocalDateTimeUtils.convertTimeToString(Integer.parseInt(latestDto.getClock()), "yyyy-MM-dd HH:mm:ss"));
            if (null != ITEM_Map.get(latestDto.getItemid())) {
                latestDto.setName(ITEM_Map.get(latestDto.getItemid()));
            }
        });
        Map<String, List<LatestDto>> collect     = latestDtos.parallelStream().collect(Collectors.groupingBy(LatestDto::getName));
        List<Map<String, Object>>    collectList = new ArrayList();
        collect.forEach((key, value) -> {
            Map<String, Object> collectMap = new HashMap<>(2);
            collectMap.put("name", key);
            collectMap.put("data", value);
            collectList.add(collectMap);
        });
        return collectList;
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

    public Map<String, Object> getAlarmNum(long timeFrom, long timeTill) {
        AlarmParam alarmParam = new AlarmParam();
        alarmParam.setRecent("false");
        List<ZbxProblemInfo> alarmList = alarmService.getAlarmList(alarmParam);
        Map<String, Object>  alarmMap  = new HashMap<>(3);

        if (ToolUtil.isNotEmpty(alarmList)) {

            alarmMap.put("total", alarmList.size());

            //过滤出指定时间段内的告警
            Map<String, Map<String, Long>> tmpMap = alarmList.parallelStream()
                    .filter(o -> !o.getSeverity().equals("0")
                            && Long.parseLong(o.getClock()) >= timeFrom
                            && Long.parseLong(o.getClock()) < timeTill
                    ).collect(
                            Collectors.groupingBy(ZbxProblemInfo::getSeverity,
                                    Collectors.groupingBy(ZbxProblemInfo::getClock, Collectors.counting()
                                    )
                            )
                    );
            List<Map<String, Object>> trendsList = new ArrayList<>();
            tmpMap.forEach((key, value) -> {
                Map<String, Object> trendsMap = new HashMap<>(2);
                List                list      = new ArrayList<>();
                value.forEach((date, val) -> {
                    Map<String, Object> valMap = new HashMap<>(2);
                    valMap.put("date", date);
                    valMap.put("val", val);
                    list.add(valMap);
                });
                trendsMap.put("name", severity[Integer.parseInt(key)]);
                trendsMap.put("data", list);
                trendsList.add(trendsMap);
            });
            alarmMap.put("trends", trendsList);
        }


        //今日开始时间
        Long       timeStart  = LocalDateTimeUtils.getSecondsByTime(LocalDateTimeUtils.getDayStart(LocalDateTime.now()));
        AlarmParam todayParam = new AlarmParam();
        todayParam.setTimeFrom(timeStart);
        List<ZbxProblemInfo> todayAlarmList = alarmService.getAlarmList(todayParam);
        Long                 todayAlarmNum  = todayAlarmList.parallelStream().filter(o -> !o.getSeverity().equals("0")).count();
        alarmMap.put("today", todayAlarmNum);

        return alarmMap;
    }
}
