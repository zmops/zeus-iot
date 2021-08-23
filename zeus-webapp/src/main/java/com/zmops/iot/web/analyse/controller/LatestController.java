package com.zmops.iot.web.analyse.controller;

import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.analyse.dto.param.LatestParam;
import com.zmops.iot.web.analyse.service.LatestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yefei
 *
 * 最新数据
 **/
@RestController
@RequestMapping("/latest")
public class LatestController {

    @Autowired
    LatestService latestService;

    @RequestMapping("/query")
    public ResponseData qeuryLatest(@Validated @RequestBody LatestParam latestParam){
        return ResponseData.success(latestService.qeuryLatest(latestParam));
    }
}
