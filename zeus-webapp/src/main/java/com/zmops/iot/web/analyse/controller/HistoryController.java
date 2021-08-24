package com.zmops.iot.web.analyse.controller;

import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.analyse.dto.param.HistoryParam;
import com.zmops.iot.web.analyse.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yefei
 * <p>
 * 历史数据
 **/
@RestController
@RequestMapping("/history")
public class HistoryController {

    @Autowired
    HistoryService historyService;

    @RequestMapping("/query")
    public ResponseData qeuryHistory(@Validated @RequestBody HistoryParam historyParam) {
        return ResponseData.success(historyService.queryHistory(historyParam));
    }
}
