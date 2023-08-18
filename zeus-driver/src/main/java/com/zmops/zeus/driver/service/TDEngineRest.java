package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.extensions.BasicAuth;
import com.dtflys.forest.http.ForestResponse;

/**
 * @author yefei
 **/
@BaseRequest(
        baseURL = "${taosUrl}"
)
public interface TDEngineRest {

    /**
     * TDEngine execute sql, 必须 2.2.0 以上版本
     *
     * @param sql
     * @return String
     */
    @Post(
            contentType = "application/json;charset=utf-8"
    )
    @BasicAuth(username = "${taosUser}", password = "${taosPwd}")
    ForestResponse<String> executeSql(@Body String sql);
}
