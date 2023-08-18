package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;
import com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor;

/**
 * @author yefei
 **/
@BaseRequest(
        baseURL = "http://${zbxServerIp}:${zbxServerPort}/zabbix/api_jsonrpc.php",
        interceptor = JsonBodyBuildInterceptor.class
)
public interface ZbxInterface {
    /**
     * 查询主机接口
     *
     * @param hostid 主机ID
     */
    @Post
    @JsonPath("/hostinterface/hostinterface.get")
    String hostinterfaceGet(@ParamName("hostid") String hostid);
}
