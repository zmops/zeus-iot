package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Post;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;

/**
 * @author nantian created at 2021/8/10 16:32
 * <p>
 * 设备离线 在线触发器，判断设备 在线，离线 状态
 */
@BaseRequest(baseURL = "${zbxApiUrl}")
public interface ZbxDeviceStatusTrigger {


    /**
     * 在线状态
     */
    @Post
    @JsonPath("/trigger/trigger.nodata.0.create")
    public String nodataOnline(@ParamName("hostName") String hostName,
                               @ParamName("itemKey") String itemKey,
                               @ParamName("nodatTime") String nodataTime,
                               @ParamName("triggerName") String triggerName);


    /**
     * 离线状态
     */
    @Post
    @JsonPath("/trigger/trigger.nodata.1.create")
    public String nodataOffline(@ParamName("hostName") String hostName,
                                @ParamName("itemKey") String itemKey,
                                @ParamName("nodatTime") String nodataTime,
                                @ParamName("triggerName") String triggerName);


    /**
     * 自定义状态，最新值等于 ？ 触发
     */
    @Post
    @JsonPath("/trigger/trigger.last.create")
    public String lastValueJudge(@ParamName("hostName") String hostName,
                                 @ParamName("itemKey") String itemKey,
                                 @ParamName("dataValue") String dataValue,
                                 @ParamName("triggerName") String triggerName);


}
