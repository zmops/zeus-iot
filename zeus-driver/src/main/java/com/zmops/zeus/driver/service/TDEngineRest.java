package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.extensions.BasicAuth;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;
import com.zmops.zeus.driver.inteceptor.TaosJsonBodyBuildInterceptor;

/**
 * @author yefei
 **/
@BaseRequest(
        baseURL = "${taosUrl}",
        interceptor = TaosJsonBodyBuildInterceptor.class
)
public interface TDEngineRest {

    /**
     * 执行sql
     *
     * @param sql
     * @return String
     */
    @Post(headers = "authTag: noAuth")
    @JsonPath("/tdengine/execute")
    @BasicAuth(username = "${taosUser}", password = "${taosPwd}")
    String executeSql(@ParamName("sql") String sql);
}
