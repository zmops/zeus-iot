package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor;

/**
 * @author nantian created at 2021/8/3 11:58
 */

@BaseRequest(
        baseURL = "${zbxApiUrl}",
        interceptor = JsonBodyBuildInterceptor.class
)
public interface ZbxApiInfo {

    /**
     * 接口信息
     *
     * @return String apiinfo
     */
    @Post(headers = "authTag: noAuth")
    @JsonPath("/apiinfo/apiinfo")
    public String getApiInfo();
}
