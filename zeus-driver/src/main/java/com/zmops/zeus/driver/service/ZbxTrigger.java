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
     * @param triggerId  触发器ID
     * @param expression 触发器 表达式
     * @param ruleLevel  告警等级
     * @return
     */
    @Post
    @JsonPath("/trigger/trigger.update")
    String triggerUpdate(@ParamName("triggerId") String triggerId,
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
    String triggerTagCreate(@ParamName("triggerId") String triggerId,
                            @ParamName("tagMap") Map<String, String> tags);

    /**
     * 根据TRIGGER ID查询触发器
     *
     * @param triggerIds 触发器IDs
     */
    @Post
    @JsonPath("/trigger/trigger.get")
    String triggerGet(@ParamName("triggerIds") String triggerIds);

    /**
     * 根据TRIGGER ID查询触发器及触发器标签
     *
     * @param triggerIds 触发器IDs
     */
    @Post
    @JsonPath("/trigger/triggerAndTags.get")
    String triggerAndTagsGet(@ParamName("triggerIds") String triggerIds);

    /**
     * 根据host 查询触发器
     *
     * @param host 设备ID
     */
    @Post
    @JsonPath("/trigger/trigger.get")
    String triggerGetByHost(@ParamName("host") String host);


    /**
     * 修改触发器状态
     *
     * @param triggerId
     * @param status
     * @return
     */
    @Post
    @JsonPath("/trigger/trigger.status.update")
    String triggerStatusUpdate(@ParamName("triggerid") String triggerId,
                               @ParamName("status") String status);
}
