package com.zmops.iot.web.transfer.controller;

import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.proxy.query.QProxy;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.transfer.dto.TransferDto;
import com.zmops.iot.web.transfer.dto.param.TransferParam;
import com.zmops.iot.web.transfer.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yefei
 * <p>
 * 数据转换服务
 */

@RestController
@RequestMapping("/transfer")
public class TransferController {

    @Autowired
    TransferService proxyService;

    /**
     * 数据转换分页列表
     */
    @GetMapping("/list")
    public ResponseData list() {
        return ResponseData.success(proxyService.list());
    }


    /**
     * 数据转换创建
     */
    @RequestMapping("/create")
    public ResponseData create() {
        proxyService.create();
        return ResponseData.success();
    }

    /**
     * 数据转换删除
     */
    @RequestMapping("/delete")
    public ResponseData delete(@Validated(BaseEntity.Delete.class) @RequestBody TransferParam transferParam) {
        proxyService.delete(transferParam.getNames());
        return ResponseData.success();
    }

    /**
     * 数据转换启动
     */
    @RequestMapping("/run")
    public ResponseData run(@Validated @RequestBody TransferParam transferParam) {
        proxyService.run(transferParam);
        return ResponseData.success();
    }

    /**
     * 数据转换停止
     */
    @RequestMapping("/stop")
    public ResponseData stop(@Validated @RequestBody TransferParam transferParam) {
        proxyService.stop(transferParam);
        return ResponseData.success();
    }
}
