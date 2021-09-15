package com.zmops.zeus.driver.service;


import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;
import com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor;

import java.util.List;
import java.util.Map;

@BaseRequest(
        baseURL = "${zbxApiUrl}",
        interceptor = JsonBodyBuildInterceptor.class
)
public interface ZbxTrigger {

    /**
     * 创建告警触发器
     *
     * @param triggerName 触发器名称
     * @param expression  触发器 表达式
     * @param ruleLevel   告警等级
     * @return
     */
    @Post
    @JsonPath("/trigger/trigger.create")
    String triggerCreate(@ParamName("triggerName") String triggerName,
                         @ParamName("expression") String expression,
                         @ParamName("ruleLevel") Byte ruleLevel);


    /**
     * 更新告警触发器
     *
     * @param triggerId   触发器ID
     * @param expression  触发器 表达式
     * @param ruleLevel   告警等级
     * @return
     */
    @Post
    @JsonPath("/trigger/trigger.update")
    String triggerUpdate(@ParamName("triggerId") Integer triggerId,
                         @ParamName("expression") String expression,
                         @ParamName("ruleLevel") Byte ruleLevel);


    /**
     * 更新创建 触发器 Tag
     *
     * @param triggerId 触发器ID
     * @param tags      标签
     * @return
     */
    @Post
    @JsonPath("/trigger/trigger.tags.update")
    String triggerTagCreate(@ParamName("triggerId") Integer triggerId,
                            @ParamName("tagMap") Map<String, String> tags);
}
