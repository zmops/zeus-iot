package com.zmops.zeus.driver.service;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.http.ForestResponse;
import com.zmops.zeus.driver.annotation.JsonPath;
import com.zmops.zeus.driver.annotation.ParamName;
import com.zmops.zeus.driver.inteceptor.JsonBodyBuildInterceptor;


@BaseRequest(baseURL = "http://172.16.50.1:3000/logkit")
public interface TransferKit {



    /**
     *  配置信息
     *
     * @return
     */
    @Get(url = "/configs")
    String getConfigs();


    /**
     *  状态信息
     *
     * @return
     */
    @Get(url = "/status")
    String getStatus();

    @Post(url = "/configs/${runnerName}",
            interceptor = JsonBodyBuildInterceptor.class)
    @JsonPath("/transfer/create")
    String createRunner(@DataVariable("runnerName") String runnerName,
                        @ParamName("runnerName") String name,
                        @ParamName("logPath") String logPath,
                        @ParamName("batchInterval") Integer batchInterval,
                        @ParamName("batchSize") Integer batchSize);

    @Put(url = "/configs/${runnerName}",
            interceptor = JsonBodyBuildInterceptor.class)
    @JsonPath("/transfer/create")
    String updateRunner(@DataVariable("runnerName") String runnerName,
                        @ParamName("runnerName") String name,
                        @ParamName("logPath") String logPath,
                        @ParamName("httpSendUrl") String httpSendUrl);

    /**
     * 关闭 采集器 Runner
     *
     * @param runnerName 采集器 runner 名称
     * @return
     */
    @Post(url = "/configs/${runnerName}/stop")
    ForestResponse<String> stopRunner(@DataVariable("runnerName") String runnerName);


    /**
     * 开启 采集器 Runner
     *
     * @param runnerName 采集器 runner 名称
     * @return
     */
    @Post(url = "/configs/${runnerName}/start")
    ForestResponse<String> startRunner(@DataVariable("runnerName") String runnerName);


    /**
     * 重置 采集器 Runner
     *
     * @param runnerName 采集器 runner 名称
     * @return
     */
    @Post(url = "/configs/${runnerName}/reset")
    ForestResponse<String> resetRunner(@DataVariable("runnerName") String runnerName);


    /**
     * 开启 采集器 Runner
     *
     * @param runnerName 采集器 runner 名称
     * @return
     */
    @Delete(url = "/configs/${runnerName}")
    ForestResponse<String> deleteRunner(@DataVariable("runnerName") String runnerName);
}
