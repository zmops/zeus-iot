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
import com.zmops.iot.web.analyse.dto.param.LatestParam;
import com.zmops.zeus.driver.service.ZbxHistoryGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

        //取出属性对应的ItemID
        List<String> zbxIds = list.parallelStream().map(ProductAttribute::getZbxId).collect(Collectors.toList());
        Map<String, List<ProductAttribute>> valueTypeMap = list.parallelStream().collect(Collectors.groupingBy(ProductAttribute::getValueType));
        Map<String, ProductAttribute> itemIdMap = list.parallelStream().collect(Collectors.toMap(ProductAttribute::getZbxId, o -> o));

        List<LatestDto> latestDtos = new ArrayList<>();

        //根据属性值类型 分组查询最新数据
        for (Map.Entry<String, List<ProductAttribute>> map : valueTypeMap.entrySet()) {
            String res = zbxHistoryGet.historyGet(one.getZbxId(), zbxIds, map.getValue().size(), Integer.parseInt(map.getKey()), null, null);
            latestDtos.addAll(JSONObject.parseArray(res, LatestDto.class));
        }

        //根据itemid去重
        latestDtos = latestDtos.parallelStream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(LatestDto::getItemid))),
                        ArrayList::new)
        );

        latestDtos.forEach(latestDto -> {
            latestDto.setClock(LocalDateTimeUtils.convertTimeToString(Integer.parseInt(latestDto.getClock()), "yyyy-MM-dd HH:mm:ss"));
            if (null != itemIdMap.get(latestDto.getItemid())) {
                latestDto.setName(itemIdMap.get(latestDto.getItemid()).getName());
                latestDto.setAttrId(itemIdMap.get(latestDto.getItemid()).getAttrId());
                latestDto.setUnits(itemIdMap.get(latestDto.getItemid()).getUnits());
                latestDto.setKey(itemIdMap.get(latestDto.getItemid()).getKey());
            }
        });

        return latestDtos;
    }

    public Map<String, Object> queryMap(String deviceId) {
        List<LatestDto> latestDtos = qeuryLatest(deviceId, Collections.emptyList());
        if (ToolUtil.isEmpty(latestDtos)) {
            return new HashMap<>(0);
        }
        return latestDtos.parallelStream().collect(Collectors.toMap(LatestDto::getKey, LatestDto::getValue));
    }
}
