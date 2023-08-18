package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;
import com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor;

import java.util.List;

/**
 * @author nantian created at 2021/8/3 14:50
 */

@BaseRequest(
        baseURL = "http://${zbxServerIp}:${zbxServerPort}${zbxApiUrl}",
        interceptor = JsonBodyBuildInterceptor.class
)
public interface ZbxHistoryGet {

    @Post
    @JsonPath("/history/history.get")
    String historyGet(@ParamName("hostid") String hostid,
                      @ParamName("itemids") List<String> itemids,
                      @ParamName("hisNum") Integer hisNum,
                      @ParamName("valueType") Integer valueType,
                      @ParamName("timeFrom") Long timeFrom,
                      @ParamName("timeTill") Long timeTill);

    @Post(headers = "authTag: noAuth")
    @JsonPath("/history/history.get")
    String historyGetWithNoAuth(@ParamName("hostid") String hostid,
                                @ParamName("itemids") List<String> itemids,
                                @ParamName("hisNum") Integer hisNum,
                                @ParamName("valueType") Integer valueType,
                                @ParamName("userAuth") String zbxApiToken);
}
