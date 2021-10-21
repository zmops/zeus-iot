package com.zmops.iot.web.analyse.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.http.ForestResponse;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.DeviceOnlineReport;
import com.zmops.iot.domain.device.ServiceExecuteRecord;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.device.query.QDeviceOnlineReport;
import com.zmops.iot.domain.device.query.QServiceExecuteRecord;
import com.zmops.iot.domain.product.query.QProduct;
import com.zmops.iot.enums.SeverityEnum;
import com.zmops.iot.enums.ValueType;
import com.zmops.iot.util.LocalDateTimeUtils;
import com.zmops.iot.util.ParseUtil;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.alarm.dto.AlarmDto;
import com.zmops.iot.web.alarm.dto.param.AlarmParam;
import com.zmops.iot.web.alarm.service.AlarmService;
import com.zmops.iot.web.analyse.dto.LatestDto;
import com.zmops.iot.web.device.dto.DeviceDto;
import com.zmops.iot.web.device.dto.TaosResponseData;
import com.zmops.zeus.driver.entity.ZbxItemInfo;
import com.zmops.zeus.driver.entity.ZbxProblemInfo;
import com.zmops.zeus.driver.service.TDEngineRest;
import com.zmops.zeus.driver.service.ZbxHost;
import com.zmops.zeus.driver.service.ZbxItem;
import io.ebean.DB;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    @Autowired
    TDEngineRest tdEngineRest;

    private static String hostId = "";

    private static final Map<String, String> ITEM_Map = new ConcurrentHashMap<>(5);

    //Zbx 指标取数速率 key
    private static final String KEY = "zabbix[wcache,values";

    /**
     * 服务器取数速率统计
     *
     * @return
     */
    public List<Map<String, Object>> collectionRate(long timeFrom, long timeTill) {
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

        List<LatestDto> latestDtos = new ArrayList<>();
        ITEM_Map.forEach((key, value) -> {
            latestDtos.addAll(historyService.queryHitoryData(hostId, Collections.singletonList(key), 10000, 0, timeFrom, timeTill));
        });

        latestDtos.forEach(latestDto -> {
            latestDto.setClock(LocalDateTimeUtils.convertTimeToString(Integer.parseInt(latestDto.getClock()), "yyyy-MM-dd"));
            if (null != ITEM_Map.get(latestDto.getItemid())) {
                latestDto.setName(ITEM_Map.get(latestDto.getItemid()));
            }
        });

        Map<String, Map<String, Double>> collect = latestDtos.parallelStream().collect(
                Collectors.groupingBy(LatestDto::getName, Collectors.groupingBy(
                        LatestDto::getClock, Collectors.averagingDouble(o -> Double.parseDouble(o.getValue()))))
        );

        List<Map<String, Object>> collectList = new ArrayList<>();
        collect.forEach((key, value) -> {
            Map<String, Object> collectMap = new HashMap<>(2);
            List<Map<String, Object>> tmpList = new ArrayList<>();
            value.forEach((date, val) -> {
                Map<String, Object> valMap = new HashMap<>(2);
                valMap.put("date", date);
                valMap.put("val", val);
                tmpList.add(valMap);
            });
            List<Map<String, Object>> dataList = tmpList.parallelStream().sorted(
                    Comparator.comparing(o -> o.get("date").toString())).collect(Collectors.toList());

            collectMap.put("name", ValueType.getVal(key));
            collectMap.put("data", dataList);
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
        String response = zbxHost.hostGet("Zabbix server");
        List<Map<String, String>> ids = JSON.parseObject(response, List.class);
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
        String itemList = zbxItem.getItemList(KEY, hostId);
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
    public Map<String, Object> getDeviceNum(Integer timeFrom, Integer timeTill) {
        Map<String, Object> deviceNumMap = new HashMap<>(4);

        List<Device> list = new QDevice().findList();
        deviceNumMap.put("total", list.size());
        deviceNumMap.put("disable", (int) list.parallelStream().filter(o -> "DISABLE".equals(o.getStatus())).count());
        deviceNumMap.put("online", (int) list.parallelStream().filter(o -> null != o.getOnline() && 1 == o.getOnline()).count());
        deviceNumMap.put("product", new QProduct().findCount());

        List<DeviceOnlineReport> onLineList = new QDeviceOnlineReport()
                .createTime.ge(LocalDateTimeUtils.convertTimeToString(timeFrom, "yyyy-MM-dd"))
                .createTime.lt(LocalDateTimeUtils.convertTimeToString(timeTill, "yyyy-MM-dd")).findList();

        Map<String, Long> collect = onLineList.parallelStream()
                .collect(Collectors.groupingBy(DeviceOnlineReport::getCreateTime, Collectors.summingLong(DeviceOnlineReport::getOnline)));

        List<Map<String, Object>> trendsList = new ArrayList<>();

        collect.forEach((key, value) -> {
            Map<String, Object> map = new ConcurrentHashMap<>(2);
            map.put("date", key);
            map.put("val", value);
            trendsList.add(map);
        });
        List<Map<String, Object>> result = trendsList.parallelStream()
                .sorted(Comparator.comparing(o -> o.get("date").toString())).collect(Collectors.toList());
        deviceNumMap.put("trends", result);

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
        List<ZbxProblemInfo> alarmList = alarmService.getZbxAlarm(alarmParam);
        Map<String, Object> alarmMap = new ConcurrentHashMap<>(3);

        if (ToolUtil.isNotEmpty(alarmList)) {
            Map<String, Map<String, Long>> initMap = new ConcurrentHashMap<>(5);
            for (String s : severity) {
                if (ToolUtil.isEmpty(s)) {
                    continue;
                }
                initMap.put(s, initMap(timeFrom, timeTill));
            }

            alarmMap.put("total", alarmList.size());
            Collections.reverse(alarmList);
            //过滤出指定时间段内的告警 并顺序排序
            Map<String, Map<String, Long>> tmpMap = alarmList.parallelStream()
                    .filter(o -> !o.getSeverity().equals("0")
                            && Long.parseLong(o.getClock()) >= timeFrom
                            && Long.parseLong(o.getClock()) < timeTill
                    ).collect(
                            Collectors.groupingBy(ZbxProblemInfo::getSeverity, Collectors.groupingBy(
                                    o -> LocalDateTimeUtils.convertTimeToString(Integer.parseInt(o.getClock()), "yyyy-MM-dd"), Collectors.counting())));

            List<Map<String, Object>> trendsList = new ArrayList<>();
            initMap.forEach((key, value) -> {
                Map<String, Object> trendsMap = new ConcurrentHashMap<>(2);

                List<Map<String, Object>> list = new ArrayList<>();
                value.forEach((date, val) -> {
                    Map<String, Object> valMap = new ConcurrentHashMap<>(2);
                    valMap.put("date", date);
                    valMap.put("val", Optional.ofNullable(tmpMap.get(SeverityEnum.getVal(key))).map(o -> o.get(date)).orElse(val));
                    list.add(valMap);
                });
                list.sort(Comparator.comparing(o -> o.get("date").toString()));
                trendsMap.put("name", key);
                trendsMap.put("data", list);
                trendsList.add(trendsMap);
            });

            alarmMap.put("trends", trendsList);
        }


        //今日开始时间
        Long timeStart = LocalDateTimeUtils.getSecondsByTime(LocalDateTimeUtils.getDayStart(LocalDateTime.now()));
        AlarmParam todayParam = new AlarmParam();
        todayParam.setTimeFrom(timeStart);
        List<ZbxProblemInfo> todayAlarmList = alarmService.getZbxAlarm(todayParam);
        Long todayAlarmNum = todayAlarmList.parallelStream().filter(o -> !o.getSeverity().equals("0")).count();
        alarmMap.put("today", todayAlarmNum);

        return alarmMap;
    }

    /**
     * 事件数量统计
     */
    public Map<String, Object> getEventNum(long timeFrom, long timeTill) {
        AlarmParam alarmParam = new AlarmParam();
        List<ZbxProblemInfo> alarmList = alarmService.getEventProblem(alarmParam);
        Map<String, Object> alarmMap = new ConcurrentHashMap<>(3);

        if (ToolUtil.isNotEmpty(alarmList)) {

            alarmMap.put("total", alarmList.size());
            Collections.reverse(alarmList);
            //过滤出指定时间段内的告警 并顺序排序
            Map<String, Long> tmpMap = alarmList.parallelStream()
                    .filter(o -> !o.getSeverity().equals("0")
                            && Long.parseLong(o.getClock()) >= timeFrom
                            && Long.parseLong(o.getClock()) < timeTill
                    ).collect(
                            Collectors.groupingBy(
                                    o -> LocalDateTimeUtils.convertTimeToString(Integer.parseInt(o.getClock()), "yyyy-MM-dd"), Collectors.counting()));

            List<Map<String, Object>> trendsList = new ArrayList<>();
            Map<String, Long> initMap = initMap(timeFrom, timeTill);

            initMap.forEach((date, val) -> {
                Map<String, Object> valMap = new ConcurrentHashMap<>(2);
                valMap.put("date", date);

                valMap.put("val", Optional.ofNullable(tmpMap.get(date)).orElse(val));
                trendsList.add(valMap);
            });
            trendsList.sort(Comparator.comparing(o -> o.get("date").toString()));
            alarmMap.put("trends", trendsList);
        }


        //今日开始时间
        Long timeStart = LocalDateTimeUtils.getSecondsByTime(LocalDateTimeUtils.getDayStart(LocalDateTime.now()));
        AlarmParam todayParam = new AlarmParam();
        todayParam.setTimeFrom(timeStart);

        List<ZbxProblemInfo> todayAlarmList = alarmService.getZbxAlarm(todayParam);
        Long todayAlarmNum = todayAlarmList.parallelStream().count();
        alarmMap.put("today", todayAlarmNum);

        return alarmMap;
    }

    /**
     * 告警TOP统计
     */
    public List<Map<String, Object>> getAlarmTop(long timeFrom, long timeTill) {
        AlarmParam alarmParam = new AlarmParam();
        alarmParam.setTimeTill(timeTill);
        alarmParam.setTimeFrom(timeFrom);

        List<ZbxProblemInfo> alarmList = alarmService.getZbxAlarm(alarmParam);

        Map<String, Long> tmpMap = new ConcurrentHashMap<>();

        if (ToolUtil.isNotEmpty(alarmList)) {

            List<String> triggerIds = alarmList.parallelStream()
                    .map(ZbxProblemInfo::getObjectid).map(Objects::toString).collect(Collectors.toList());

            List<DeviceDto> deviceList = DB.findDto(DeviceDto.class,
                    "select name,r.zbx_id from device d INNER JOIN (select relation_id,zbx_id from product_event_relation where zbx_id in (:zbxIds)) r on r.relation_id=d.device_id")
                    .setParameter("zbxIds", triggerIds).findList();

            Map<String, List<DeviceDto>> deviceMap = deviceList.parallelStream().collect(Collectors.groupingBy(DeviceDto::getZbxId));


            List<AlarmDto> alarmDtoList = new ArrayList<>();
            alarmList.forEach(zbxProblemInfo -> {
                if (ToolUtil.isNotEmpty(deviceMap.get(zbxProblemInfo.getObjectid()))) {
                    deviceMap.get(zbxProblemInfo.getObjectid()).forEach(deviceDto -> {
                        AlarmDto alarmDto = new AlarmDto();
                        BeanUtils.copyProperties(zbxProblemInfo, alarmDto);
                        alarmDto.setRClock(zbxProblemInfo.getR_clock());
                        alarmDto.setDeviceName(deviceDto.getName());
                        alarmDtoList.add(alarmDto);
                    });
                }
            });

            tmpMap = alarmDtoList.parallelStream().collect(Collectors.groupingBy(AlarmDto::getDeviceName, Collectors.counting()));
        }

        List<Map<String, Object>> topList = new ArrayList<>();
        tmpMap.forEach((key, value) -> {
            Map<String, Object> alarmMap = new ConcurrentHashMap<>(2);
            alarmMap.put("name", key);
            alarmMap.put("value", value);
            topList.add(alarmMap);
        });

        topList.sort(Comparator.comparing(o -> Integer.parseInt(o.get("value").toString())));
        topList.subList(0, Math.min(topList.size(), 5));

        return topList;
    }

    /**
     * 服务调用统计
     */
    public Map<String, Object> serviceExecuteNum(long timeFrom, long timeTill) {
        List<ServiceExecuteRecord> list = new QServiceExecuteRecord().createTime.ge(LocalDateTimeUtils.getLDTBySeconds((int) timeFrom))
                .createTime.lt(LocalDateTimeUtils.getLDTBySeconds((int) timeTill)).orderBy().createTime.asc().findList();

        Map<String, Object> executeMap = new ConcurrentHashMap<>(3);
        if (ToolUtil.isNotEmpty(list)) {
            executeMap.put("total", list.size());
            Map<String, Long> initMap = initMap(timeFrom, timeTill);
            Map<String, Long> tmpMap = list.parallelStream().collect(
                    Collectors.groupingBy(o -> LocalDateTimeUtils.formatTime(o.getCreateTime(), "yyyy-MM-dd"), Collectors.counting()));

            List<Map<String, Object>> trendsList = new ArrayList<>();
            initMap.forEach((key, value) -> {
                Map<String, Object> valMap = new ConcurrentHashMap<>(2);
                valMap.put("date", key);
                valMap.put("val", Optional.ofNullable(tmpMap.get(key)).orElse(value));
                trendsList.add(valMap);
            });
            trendsList.sort(Comparator.comparing(o -> o.get("date").toString()));
            executeMap.put("trends", trendsList);
        }

        //今日开始时间
        long timeStart = LocalDateTimeUtils.getSecondsByTime(LocalDateTimeUtils.getDayStart(LocalDateTime.now()));
        long todayNum = new QServiceExecuteRecord().createTime.ge(LocalDateTimeUtils.getLDTBySeconds((int) timeStart)).findCount();

        executeMap.put("today", todayNum);

        return executeMap;
    }

    /**
     * 数据量统计
     */
    public Map<String, Object> dataLevel() {

        Map<String, Object> dataMap = new ConcurrentHashMap<>(4);

        dataMap.put("totalRecordNum", ParseUtil.getCommaFormat(getRecordNum() + ""));
        dataMap.put("todayRecordNum", ParseUtil.getCommaFormat(getTodayRecordNum(
                LocalDateTimeUtils.formatTime(LocalDateTimeUtils.getDayStart(LocalDateTime.now()))) + ""));

        int serviceExecuteNum = new QServiceExecuteRecord().findCount();
        dataMap.put("serviceExecuteNum", ParseUtil.getCommaFormat(serviceExecuteNum + ""));

        return dataMap;
    }

    private int getRecordNum() {
        int totalRecord = 0;
        String sql = "select count(1) from history";
        totalRecord += getRecordNum(sql);

        sql = "select count(1) from history_uint";
        totalRecord += getRecordNum(sql);

        return totalRecord;
    }

    private int getTodayRecordNum(String time) {
        int totalRecord = 0;
        String sql = "select count(1) from history where clock>'" + time + "'";
        totalRecord += getRecordNum(sql);

        sql = "select count(1) from history_uint where clock>'" + time + "'";
        totalRecord += getRecordNum(sql);

        return totalRecord;
    }

    private int getRecordNum(String sql) {
        ForestResponse<String> res_history = tdEngineRest.executeSql(sql);

        if (res_history.isError()) {
            return 0;
        }

        String res = res_history.getContent();

        TaosResponseData taosResponseData = JSON.parseObject(res, TaosResponseData.class);
        String[][] data_history = taosResponseData.getData();
        if (data_history.length > 0) {
            return Integer.parseInt(data_history[0][0]);
        }
        return 0;
    }

    private Map<String, Long> initMap(long start, long end) {
        long l = LocalDateTimeUtils.betweenTwoTime(LocalDateTimeUtils.getLDTBySeconds((int) start), LocalDateTimeUtils.getLDTBySeconds((int) end), ChronoUnit.DAYS);
        Map<String, Long> map = new HashMap<>((int) l);
        map.put(LocalDateTimeUtils.formatTime(LocalDateTimeUtils.getLDTBySeconds((int) start), "yyyy-MM-dd"), 0L);
        for (long i = 1; i <= l; i++) {
            map.put(LocalDateTimeUtils.formatTime(LocalDateTimeUtils.plus(LocalDateTimeUtils.getLDTBySeconds((int) start), i, ChronoUnit.DAYS), "yyyy-MM-dd"), 0L);
        }
        return map;
    }
}
