package com.zmops.iot.web.analyse.service;

import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.ProductAttribute;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.LocalDateTimeUtils;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.analyse.dto.LatestDto;
import com.zmops.iot.web.analyse.dto.param.HistoryParam;
import com.zmops.zeus.driver.service.ZbxHistoryGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yefei
 * <p>
 * 历史数据服务
 **/
@Service
public class HistoryService {

    @Autowired
    ZbxHistoryGet zbxHistoryGet;

    public Pager<LatestDto> queryHistory(HistoryParam historyParam) {
        List<LatestDto> latestDtos = queryHistory(historyParam.getDeviceId(), historyParam.getAttrIds(),
                dateTransfer(historyParam.getTimeFrom()),
                dateTransfer(historyParam.getTimeTill()));
        List<LatestDto> collect = latestDtos.stream().skip((historyParam.getPage() - 1) * historyParam.getMaxRow())
                .limit(historyParam.getMaxRow()).collect(Collectors.toList());
        return new Pager<>(collect, latestDtos.size());
    }

    private long dateTransfer(String date) {
        LocalDateTime now = LocalDateTime.now();
        if (ToolUtil.isEmpty(date) || date.equals("now")) {
            return LocalDateTimeUtils.getSecondsByTime(now);
        }
        if (date.startsWith("now-")) {
            String value = date.substring(date.indexOf("-") + 1, date.length() - 1);
            String unit  = date.substring(date.length() - 1);
            switch (unit) {
                case "m":
                    return LocalDateTimeUtils.getSecondsByTime(LocalDateTimeUtils.minu(now, Integer.valueOf(value), ChronoUnit.MINUTES));
                case "d":
                    return LocalDateTimeUtils.getSecondsByTime(LocalDateTimeUtils.minu(now, Integer.valueOf(value), ChronoUnit.DAYS));
                case "M":
                    return LocalDateTimeUtils.getSecondsByTime(LocalDateTimeUtils.minu(now, Integer.valueOf(value), ChronoUnit.MONTHS));
                case "h":
                    return LocalDateTimeUtils.getSecondsByTime(LocalDateTimeUtils.minu(now, Integer.valueOf(value), ChronoUnit.HOURS));
                case "y":
                    return LocalDateTimeUtils.getSecondsByTime(LocalDateTimeUtils.minu(now, Integer.valueOf(value), ChronoUnit.YEARS));
                default:
                    return LocalDateTimeUtils.getSecondsByTime(now);
            }
        } else {
            return LocalDateTimeUtils.getSecondsByStr(date);
        }
    }

    public List<LatestDto> queryHistory(String deviceId, List<Long> attrIds, Long timeFrom, Long timeTill) {
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
        //取出属性对应的ItemID
        List<String>                        zbxIds       = list.parallelStream().map(ProductAttribute::getZbxId).collect(Collectors.toList());
        Map<String, List<ProductAttribute>> valueTypeMap = list.parallelStream().collect(Collectors.groupingBy(ProductAttribute::getValueType));
        Map<String, ProductAttribute>       itemIdMap    = list.parallelStream().collect(Collectors.toMap(ProductAttribute::getZbxId, o -> o));
        List<LatestDto>                     latestDtos   = new ArrayList<>();
        if (null == timeFrom) {
            timeFrom = LocalDateTimeUtils.getSecondsByTime(LocalDateTimeUtils.getDayStart(LocalDateTime.now()));
        }
        //根据属性值类型 分组查询历史数据
        for (Map.Entry<String, List<ProductAttribute>> map : valueTypeMap.entrySet()) {
            latestDtos.addAll(queryHitoryData(one.getZbxId(), zbxIds, 1000, Integer.parseInt(map.getKey()), timeFrom, timeTill));
        }

        latestDtos.forEach(latestDto -> {
            latestDto.setClock(LocalDateTimeUtils.convertTimeToString(Integer.parseInt(latestDto.getClock()), "yyyy-MM-dd HH:mm:ss"));
            if (null != itemIdMap.get(latestDto.getItemid())) {
                latestDto.setName(itemIdMap.get(latestDto.getItemid()).getName());
                latestDto.setAttrId(itemIdMap.get(latestDto.getItemid()).getAttrId());
                latestDto.setUnits(itemIdMap.get(latestDto.getItemid()).getUnits());
            }
        });

        return latestDtos;
    }

    public List<LatestDto> queryHitoryData(String hostId, List<String> itemIds, int hisNum, Integer valueType, Long timeFrom, Long timeTill) {
        String res = zbxHistoryGet.historyGet(hostId, itemIds, hisNum, valueType, timeFrom, timeTill);
        return JSONObject.parseArray(res, LatestDto.class);
    }

}
