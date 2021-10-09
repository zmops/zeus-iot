package com.zmops.iot.web.analyse.controller;

import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.analyse.dto.LatestDto;
import com.zmops.iot.web.analyse.dto.param.LatestParam;
import com.zmops.iot.web.analyse.service.LatestService;
import com.zmops.iot.web.auth.Permission;
import com.zmops.zeus.driver.service.TDEngineRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yefei
 * <p>
 * 最新数据
 **/
@RestController
@RequestMapping("/latest")
public class LatestController {

    @Autowired
    LatestService latestService;

    @Autowired
    private TDEngineRest tdEngineRest;

    @RequestMapping("/query")
    @Permission(code = "latest")
    public Pager<LatestDto> qeuryLatest(@Validated @RequestBody LatestParam latestParam) {
        return latestService.qeuryLatest(latestParam);
    }

    @RequestMapping("/queryMap")
    @Permission(code = "latest")
    public ResponseData queryMap(@RequestParam("deviceId") String deviceId) {
        return ResponseData.success(latestService.queryMap(deviceId));
    }


    @RequestMapping("/tdexecutesql")
    public ResponseData getTdEngineData(@RequestParam String sql) {
        return ResponseData.success(tdEngineRest.executeSql(sql));
    }
}
