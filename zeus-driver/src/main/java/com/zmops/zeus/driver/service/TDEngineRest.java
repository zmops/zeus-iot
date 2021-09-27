package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.dtflys.forest.extensions.BasicAuth;

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
    @Post
    @BasicAuth(username = "${taosUser}", password = "${taosPwd}")
    String executeSql(String sql);
}
