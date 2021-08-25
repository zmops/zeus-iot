package com.zmops.iot.web.analyse.service;

import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.ProductAttribute;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.analyse.dto.LatestDto;
import com.zmops.iot.web.analyse.dto.param.LatestParam;
import com.zmops.zeus.driver.service.ZbxHistoryGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    public List<LatestDto> qeuryLatest(LatestParam latestParam) {
        List<LatestDto> latestDtos = qeuryLatest(latestParam.getDeviceId(), latestParam.getAttrIds());
        return latestDtos;
    }

    public List<LatestDto> qeuryLatest(Long deviceId, List<Long> attrIds) {
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
        //根据属性值类型 分组查询最新数据
        for (Map.Entry<String, List<ProductAttribute>> map : valueTypeMap.entrySet()) {
            String res = zbxHistoryGet.historyGet(one.getZbxId(), zbxIds, map.getValue().size(), Integer.parseInt(map.getKey()), null, null);
            latestDtos.addAll(JSONObject.parseArray(res, LatestDto.class));
        }

        latestDtos.forEach(latestDto -> {
            if (null != itemIdMap.get(latestDto.getItemid())) {
                latestDto.setName(itemIdMap.get(latestDto.getItemid()).getName());
                latestDto.setAttrId(itemIdMap.get(latestDto.getItemid()).getAttrId());
                latestDto.setUnits(itemIdMap.get(latestDto.getItemid()).getUnits());
            }
        });

        return latestDtos;
    }
}
