package com.zmops.iot.web.device.schedule;

import com.alibaba.fastjson.JSON;
import com.zmops.iot.domain.device.Tag;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.device.query.QTag;
import com.zmops.iot.domain.product.query.QProductAttribute;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.TaosResponseData;
import com.zmops.zeus.driver.service.TDEngineRest;
import com.zmops.zeus.driver.service.ZbxItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yefei
 **/
@EnableScheduling
@Component
@Slf4j
public class SyncTaosTagSchedule {

    @Autowired
    ZbxItem zbxItem;

    @Autowired
    TDEngineRest tdEngineRest;

    @Scheduled(cron = "0 55 23 1/1 * ? ")
//    @Scheduled(cron = "0 0/1 * * * ? ")
    public void sync() {
        List<String> deviceIds = new QDevice().select(QDevice.alias().deviceId).findSingleAttributeList();
        List<Tag>    tagList   = new QTag().sid.in(deviceIds).findList();
        if (ToolUtil.isEmpty(tagList)) {
            return;
        }
        Map<String, List<Tag>> tagMap   = tagList.parallelStream().collect(Collectors.groupingBy(Tag::getSid));
        String                 describe = tdEngineRest.executeSql("DESCRIBE history;");

        TaosResponseData taosResponseData = JSON.parseObject(describe, TaosResponseData.class);
        String[][]       data             = taosResponseData.getData();

        List<String> taosTagNames = new ArrayList<>();
        for (String[] datum : data) {
            if (ToolUtil.isNotEmpty(datum[3]) && "TAG".equals(datum[3])) {
                taosTagNames.add(datum[0]);
            }
        }

        if (ToolUtil.isEmpty(taosTagNames)) {
            return;
        }

        List<String> tagNames = tagList.parallelStream().map(Tag::getTag).collect(Collectors.toList());
        //新增taos没有的标签
        addTaosTag(tagNames, taosTagNames);
        //删除taos标签
        delTaosTag(taosTagNames, tagNames);

        //更新子表 标签值
        tagMap.forEach((deviceId, tags) -> {
            //根据deviceId 找到 设备下所有itemId
            List<String> itemIds = new QProductAttribute().select(QProductAttribute.alias().zbxId).productId.eq(deviceId).findSingleAttributeList();
            if (ToolUtil.isEmpty(itemIds)) {
                return;
            }
            itemIds.forEach(itemId -> {
                tags.forEach(tag -> {
                    tdEngineRest.executeSql("ALTER TABLE h_" + itemId + " SET TAG " + tag.getTag() + "='" + tag.getValue() + "'");
                });
            });

        });
    }

    private void delTaosTag(List<String> list1, List<String> list2) {
        List<String> tagNames     = list2;
        List<String> taosTagNames = list1;

        taosTagNames.removeAll(tagNames);

        if (ToolUtil.isEmpty(taosTagNames)) {
            return;
        }

        taosTagNames.forEach(tagName -> {
            if ("deviceid".equals(tagName) || "itemid".equals(tagName)) {
                return;
            }
            tdEngineRest.executeSql("ALTER STABLE history_uint DROP  TAG '" + tagName + "'");
            tdEngineRest.executeSql("ALTER STABLE history DROP  TAG '" + tagName + "'");
        });
    }

    private void addTaosTag(List<String> list1, List<String> list2) {
        List<String> tagNames     = list1;
        List<String> taosTagNames = list2;

        tagNames.removeAll(taosTagNames);
        if (ToolUtil.isEmpty(tagNames)) {
            return;
        }

        tagNames.forEach(tagName -> {
            tdEngineRest.executeSql("ALTER STABLE history_uint ADD TAG " + tagName + " NCHAR(16)");
            tdEngineRest.executeSql("ALTER STABLE history ADD TAG " + tagName + " NCHAR(16)");
        });
    }

}
