package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;
import com.zmops.zeus.driver.entity.ZbxProcessingStep;

import java.util.List;
import java.util.Map;

/**
 * @author nantian created at 2021/8/4 18:07
 */

@BaseRequest(baseURL = "${zbxApiUrl}")
public interface ZbxItem {


    /**
     * 创建 Zabbix Trapper ITEM
     *
     * @param itemName           名称
     * @param itemKey            唯一Key
     * @param hostId             主机ID
     * @param valueType          值类型 0 和 3 数字
     * @param units              单位
     * @param processingStepList 预处理步骤
     * @param valuemapid         值映射ID
     * @param tagMap             标签
     * @return String
     */
    @Post
    @JsonPath("/item/item.trapper.create")
    String createTrapperItem(@ParamName("itemName") String itemName,
                             @ParamName("itemKey") String itemKey,
                             @ParamName("hostId") String hostId,
                             @ParamName("valueType") String valueType,
                             @ParamName("units") String units,
                             @ParamName("processList") List<ZbxProcessingStep> processingStepList,
                             @ParamName("valuemapid") String valuemapid,
                             @ParamName("tagMap") Map<String, String> tagMap);

    /**
     * 修改 Zabbix Trapper ITEM
     *
     * @param itemid             ID
     * @param itemName           名称
     * @param itemKey            唯一Key
     * @param hostId             主机ID
     * @param valueType          值类型 0 和 3 数字
     * @param units              单位
     * @param processingStepList 预处理步骤
     * @param valuemapid         值映射ID
     * @param tagMap             标签
     * @return String
     */
    @Post
    @JsonPath("/item/item.trapper.update")
    String updateTrapperItem(@ParamName("itemid") String itemid,
                             @ParamName("itemName") String itemName,
                             @ParamName("itemKey") String itemKey,
                             @ParamName("hostId") String hostId,
                             @ParamName("valueType") String valueType,
                             @ParamName("units") String units,
                             @ParamName("processList") List<ZbxProcessingStep> processingStepList,
                             @ParamName("valuemapid") String valuemapid,
                             @ParamName("tagMap") Map<String, String> tagMap);

    /**
     * 删称 Zabbix Trapper ITEM
     *
     * @param itemIds ID
     */
    @Post
    @JsonPath("/item/item.trapper.delete")
    String deleteTrapperItem(@ParamName("itemIds") List<String> itemIds);

    /**
     * 根据itemid 获取 ITEM 基本信息
     *
     * @param itemId itemid
     * @return String
     */
    @Post
    @JsonPath("/item/item.get")
    String getItemInfo(@ParamName("itemId") String itemId, @ParamName("hostid") String hostid);

    /**
     * 根据item name 获取 ITEM 信息
     *
     * @param key key
     * @return String
     */
    @Post
    @JsonPath("/item/item.get")
    String getItemList(@ParamName("key") String key, @ParamName("hostid") String hostid);
}
