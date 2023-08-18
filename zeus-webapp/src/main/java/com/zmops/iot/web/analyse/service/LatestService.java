package com.zmops.iot.web.analyse.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtflys.forest.http.ForestResponse;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.ProductAttribute;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.LocalDateTimeUtils;
import com.zmops.iot.util.ParseUtil;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.analyse.dto.LatestDto;
import com.zmops.iot.web.analyse.dto.Mapping;
import com.zmops.iot.web.analyse.dto.ValueMap;
import com.zmops.iot.web.analyse.dto.param.LatestParam;
import com.zmops.iot.web.device.dto.TaosResponseData;
import com.zmops.iot.web.init.BasicSettingsInit;
import com.zmops.zeus.driver.service.TDEngineRest;
import com.zmops.zeus.driver.service.ZbxHistoryGet;
import com.zmops.zeus.driver.service.ZbxValueMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 最新数据服务
 **/
@Service
public class LatestService {

    @Autowired
    ZbxHistoryGet zbxHistoryGet;

    @Autowired
    ZbxValueMap zbxValueMap;

    @Autowired
    TDEngineRest tdEngineRest;

    /**
     * 查询最新数据
     *
     * @param latestParam
     * @return
     */
    public Pager<LatestDto> qeuryLatest(LatestParam latestParam) {
        List<LatestDto> latestDtos = qeuryLatest(latestParam.getDeviceId(), latestParam.getAttrIds());
        List<LatestDto> collect = latestDtos.stream().skip((latestParam.getPage() - 1) * latestParam.getMaxRow())
                .limit(latestParam.getMaxRow()).collect(Collectors.toList());
        return new Pager<>(collect, latestDtos.size());
    }

    public List<LatestDto> qeuryLatest(String deviceId, List<Long> attrIds) {

        //查询出设备
        Device one = new QDevice().deviceId.eq(deviceId).findOne();
        if (null == one || ToolUtil.isEmpty(one.getZbxId())) {
            return Collections.emptyList();
        }

        //查询设备属性
        QProductAttribute query = new QProductAttribute().productId.eq(deviceId);
        if (ToolUtil.isNotEmpty(attrIds)) {
            query.attrId.in(attrIds);
        }

        List<ProductAttribute> list = query.findList();
        if (ToolUtil.isEmpty(list)) {
            return Collections.emptyList();
        }

        Map<String, List<ProductAttribute>> valueTypeMap = list.parallelStream().collect(Collectors.groupingBy(ProductAttribute::getValueType));
        Map<String, ProductAttribute> itemIdMap = list.parallelStream().collect(Collectors.toMap(ProductAttribute::getZbxId, o -> o, (a, b) -> a));
        List<LatestDto> latestDtos;
        if (checkTDengine()) {
            latestDtos = queryLatestFromTD(deviceId, valueTypeMap);
        } else {
            latestDtos = queryLatestFromZbx(one.getZbxId(), valueTypeMap);
        }


        //处理值映射
        List<String> valuemapids = list.parallelStream().filter(o -> null != o.getValuemapid()).map(ProductAttribute::getValuemapid).collect(Collectors.toList());
        Map<String, List<Mapping>> mappings = new ConcurrentHashMap<>(valuemapids.size());
        if (!CollectionUtils.isEmpty(valuemapids)) {
            String res = zbxValueMap.valueMapGet(valuemapids.toString());
            List<ValueMap> mappingList = JSONObject.parseArray(res, ValueMap.class);
            if (!CollectionUtils.isEmpty(mappingList)) {
                mappings = mappingList.stream().collect(Collectors.toMap(ValueMap::getValuemapid, ValueMap::getMappings, (a, b) -> a));
            }
        }

        Map<String, List<Mapping>> finalMappings = mappings;
        latestDtos.forEach(latestDto -> {
//            latestDto.setClock(LocalDateTimeUtils.convertTimeToString(Integer.parseInt(latestDto.getClock()), "yyyy-MM-dd HH:mm:ss"));
            latestDto.setOriginalValue(latestDto.getValue());
            if (null != itemIdMap.get(latestDto.getItemid())) {
                latestDto.setName(itemIdMap.get(latestDto.getItemid()).getName());
                latestDto.setAttrId(itemIdMap.get(latestDto.getItemid()).getAttrId());
                latestDto.setUnits(itemIdMap.get(latestDto.getItemid()).getUnits());
                latestDto.setKey(itemIdMap.get(latestDto.getItemid()).getKey());

                String valueMapid = itemIdMap.get(latestDto.getItemid()).getValuemapid();
                if (null != valueMapid) {
                    List<Mapping> mappingList = finalMappings.get(valueMapid);
                    if (!CollectionUtils.isEmpty(mappingList)) {
                        Map<String, String> mappingMap = mappingList.parallelStream().collect(Collectors.toMap(Mapping::getValue, Mapping::getNewvalue, (a, b) -> a));
                        latestDto.setValue(mappingMap.get(latestDto.getValue()));
                    }
                }
            }
        });

        return latestDtos;
    }

    private boolean checkTDengine() {
        String sql = String.format("select LAST_ROW(*) from history_uint where deviceid = 'Zabbix server';");

        try {
            ForestResponse<String> resHistory = tdEngineRest.executeSql(sql);

            if (resHistory.isError()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //从TDengine取数
    public List<LatestDto> queryLatestFromTD(String deviceId, Map<String, List<ProductAttribute>> valueTypeMap) {

        List<LatestDto> latestDtos = new ArrayList<>();
        //根据属性值类型 分组查询最新数据
        for (Map.Entry<String, List<ProductAttribute>> map : valueTypeMap.entrySet()) {
            //取出属性对应的ItemID
            List<String> itemIds = map.getValue().parallelStream().map(ProductAttribute::getZbxId).collect(Collectors.toList());
            latestDtos.addAll(queryLatestFromTD(deviceId, itemIds, Integer.parseInt(map.getKey())));
        }
        return latestDtos;
    }

    private List<LatestDto> queryLatestFromTD(String deviceId, List<String> itemIds, int unitType) {
        String itemids = "'" + itemIds.parallelStream().collect(Collectors.joining("','")) + "'";
        String sql = String.format("select LAST_ROW(*) from %s where deviceid = '%s' and itemid in (%s) group by itemid;", getHistoryTableName(unitType), deviceId, itemids);

        ForestResponse<String> resHistory = tdEngineRest.executeSql(sql);

        if (resHistory.isError()) {
            return Collections.emptyList();
        }

        String res = resHistory.getContent();
        TaosResponseData taosResponseData = JSON.parseObject(res, TaosResponseData.class);
        String[][] dataHistory = taosResponseData.getData();

        List<LatestDto> latestDtos = new ArrayList<>();
        if (dataHistory.length > 0) {
            for (String[] data : dataHistory) {
                LatestDto latestDto = new LatestDto();
                latestDto.setClock(LocalDateTimeUtils.formatTime(LocalDateTimeUtils.dateToStamp(data[0])));
                latestDto.setValue(ParseUtil.getFormatFloat(data[1]));
                latestDto.setItemid(data[2]);
                latestDtos.add(latestDto);
            }
        }
        return latestDtos;
    }

    private static String getHistoryTableName(int unitType) {
        switch (unitType) {
            case 0:
                return "history";
            case 1:
                return "history_str";
            case 3:
                return "history_uint";
            case 4:
                return "history_text";
            default:
                throw new UnsupportedOperationException();
        }
    }

    //从Zbx接口取数
    private List<LatestDto> queryLatestFromZbx(String zbxId, Map<String, List<ProductAttribute>> valueTypeMap) {
        List<LatestDto> latestDtos = new ArrayList<>();
        //根据属性值类型 分组查询最新数据
        for (Map.Entry<String, List<ProductAttribute>> map : valueTypeMap.entrySet()) {
            //取出属性对应的ItemID
            List<String> itemIds = map.getValue().parallelStream().map(ProductAttribute::getZbxId).collect(Collectors.toList());
            String res = zbxHistoryGet.historyGet(zbxId, itemIds, map.getValue().size(), Integer.parseInt(map.getKey()), null, null);
            latestDtos.addAll(JSONObject.parseArray(res, LatestDto.class));
        }

        //根据itemid去重
        latestDtos = latestDtos.parallelStream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(LatestDto::getItemid))),
                        ArrayList::new)
        );
        latestDtos.forEach(latestDto -> latestDto.setClock(LocalDateTimeUtils.convertTimeToString(Integer.parseInt(latestDto.getClock()), "yyyy-MM-dd HH:mm:ss")));

        return latestDtos;
    }

    /**
     * 取事件属性 最新数据
     *
     * @return List
     */
    public List<LatestDto> queryEventLatest(String hostid, List<String> zbxIds, int valueType) {

        //根据属性值类型 查询最新数据

        String res = zbxHistoryGet.historyGetWithNoAuth(hostid, zbxIds, 1, valueType, BasicSettingsInit.zbxApiToken);
        List<LatestDto> latestDtos = JSONObject.parseArray(res, LatestDto.class);

        latestDtos.forEach(latestDto -> {
            latestDto.setClock(LocalDateTimeUtils.convertTimeToString(Integer.parseInt(latestDto.getClock()), "yyyy-MM-dd HH:mm:ss"));
            latestDto.setOriginalValue(latestDto.getValue());
        });

        return latestDtos;
    }


    public Map<String, Object> queryMap(String deviceId) {
        List<LatestDto> latestDtos = qeuryLatest(deviceId, Collections.emptyList());
        if (ToolUtil.isEmpty(latestDtos)) {
            return new HashMap<>(0);
        }
        return latestDtos.parallelStream().collect(Collectors.toMap(LatestDto::getKey, LatestDto::getValue, (a, b) -> a));
    }
}
