package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;

import java.util.List;

/**
 * @author nantian created at 2021/8/3 14:50
 */

@BaseRequest(baseURL = "${zbxApiUrl}")
public interface ZbxHistoryGet {

    @Post
    @JsonPath("/history/history.get")
    String historyGet(@ParamName("hostid") String hostid,
                      @ParamName("itemids") List<String> itemids,
                      @ParamName("hisNum") Integer hisNum,
                      @ParamName("valueType") Integer valueType);
}
