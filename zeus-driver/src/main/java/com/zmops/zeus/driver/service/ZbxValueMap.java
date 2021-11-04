package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;
import com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor;

import java.util.Map;

/**
 * @author nantian created at 2021/8/4 15:04
 * <p>
 * 值映射设置
 */
@BaseRequest(
        baseURL = "http://${zbxServerIp}:${zbxServerPort}${zbxApiUrl}",
        interceptor = JsonBodyBuildInterceptor.class
)
public interface ZbxValueMap {

    /**
     * 创建值映射
     *
     * @param hostId       主机ID
     * @param valueMapName 值映射名称
     * @param valMaps      值映射 翻译map
     * @return
     */
    @Post
    @JsonPath("/valuemap/valuemap.create")
    String valueMapCreate(@ParamName("hostId") String hostId,
                          @ParamName("valueMapName") String valueMapName,
                          @ParamName("valMaps") Map<String, String> valMaps);


    /**
     * 删除值映射
     *
     * @param valueMapId 值映射ID
     * @return String
     */
    @Post
    @JsonPath("/valuemap/valuemap.delete")
    String valueMapDelete(@ParamName("valueMapId") String valueMapId);

    /**
     * 修改值映射
     *
     * @param hostId       主机ID
     * @param valueMapName 值映射名称
     * @param valMaps      值映射 翻译map
     * @param valueMapId   值映射ID
     * @return
     */
    @Post
    @JsonPath("/valuemap/valuemap.update")
    String valueMapUpdate(@ParamName("hostId") String hostId,
                          @ParamName("valueMapName") String valueMapName,
                          @ParamName("valMaps") Map<String, String> valMaps,
                          @ParamName("valueMapId") String valueMapId);

    /**
     * 删除值映射
     *
     * @param valuemapids 值映射ID
     * @return String
     */
    @Post
    @JsonPath("/valuemap/valuemap.get")
    String valueMapGet(@ParamName("valuemapids") String valuemapids);
}
