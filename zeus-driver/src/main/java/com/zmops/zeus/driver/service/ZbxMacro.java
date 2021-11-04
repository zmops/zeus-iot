package com.zmops.zeus.driver.service;


import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;
import com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor;

import java.util.List;

/**
 * 宏定义接口
 */
@BaseRequest(
        baseURL = "http://${zbxServerIp}:${zbxServerPort}${zbxApiUrl}",
        interceptor = JsonBodyBuildInterceptor.class
)
public interface ZbxMacro {


    /**
     * 创建宏
     *
     * @param hostid 主机ID
     * @param macro  宏 Key
     * @param value  宏 Value
     * @return
     */
    @Post
    @JsonPath("/macro/usermacro.create")
    String macroCreate(@ParamName("hostid") String hostid,
                       @ParamName("macro") String macro,
                       @ParamName("value") String value,
                       @ParamName("description") String description);


    /**
     * 更新 宏
     *
     * @param macroid 宏 ID
     * @param macro   宏 Key
     * @param value   宏 Value
     * @return
     */
    @Post
    @JsonPath("/macro/usermacro.update")
    String macroUpdate(@ParamName("macroid") String macroid,
                       @ParamName("macro") String macro,
                       @ParamName("value") String value,
                       @ParamName("description") String description);

    /**
     * 获取 宏
     *
     * @param hostid 主机ID
     * @return
     */
    @Post
    @JsonPath("/macro/usermacro.get")
    String macroGet(@ParamName("hostid") String hostid);


    /**
     * 搜索 宏 ID
     *
     * @param hostid 主机ID
     * @param macro  宏 Key
     * @return
     */
    @Post
    @JsonPath("/macro/usermacro.filter.get")
    String macroQuery(@ParamName("hostid") String hostid,
                      @ParamName("macro") String macro);


    /**
     * 删除宏
     *
     * @param macroids 宏IDs
     * @return
     */
    @Post
    @JsonPath("/macro/usermacro.delete")
    String macroDelete(@ParamName("macroids") List<String> macroids);

}
