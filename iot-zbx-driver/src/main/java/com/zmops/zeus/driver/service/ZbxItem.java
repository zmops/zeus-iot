package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;
import com.zmops.zeus.driver.entity.ZbxProcessingStep;

import java.util.List;

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
     * @return
     */
    @Post
    @JsonPath("/item/item.trapper.create")
    String createTrapperItem(@ParamName("itemName") String itemName,
                             @ParamName("itemKey") String itemKey,
                             @ParamName("hostId") Integer hostId,
                             @ParamName("valueType") String valueType,
                             @ParamName("units") String units,
                             @ParamName("processList") List<ZbxProcessingStep> processingStepList);


}
