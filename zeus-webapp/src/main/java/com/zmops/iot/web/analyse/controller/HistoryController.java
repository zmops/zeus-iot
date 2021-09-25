package com.zmops.iot.web.analyse.controller;

import com.zmops.iot.model.page.Pager;
import com.zmops.iot.web.analyse.dto.LatestDto;
import com.zmops.iot.web.analyse.dto.param.HistoryParam;
import com.zmops.iot.web.analyse.service.HistoryService;
import com.zmops.iot.web.auth.Permission;
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
    @Permission(code = "latest")
    public Pager<LatestDto> qeuryHistory(@Validated @RequestBody HistoryParam historyParam) {
        return historyService.queryHistory(historyParam);
    }
}
