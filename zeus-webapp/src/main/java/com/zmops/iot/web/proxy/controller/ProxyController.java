package com.zmops.iot.web.proxy.controller;

import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.proxy.query.QProxy;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.proxy.dto.ProxyDto;
import com.zmops.iot.web.proxy.dto.param.ProxyParam;
import com.zmops.iot.web.proxy.service.ProxyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yefei
 * <p>
 * 代理服务
 */

@RestController
@RequestMapping("/proxy")
public class ProxyController {

    @Autowired
    ProxyService proxyService;

    /**
     * 代理服务分页列表
     */
    @RequestMapping("/getProxyByPage")
    public Pager<ProxyDto> getProxyByPage(@RequestBody ProxyParam proxyParam) {
        return proxyService.getProxyByPage(proxyParam);
    }

    /**
     * 代理服务列表
     */
    @RequestMapping("/list")
    public ResponseData list(@RequestBody ProxyParam proxyParam) {
        return ResponseData.success(proxyService.list(proxyParam));
    }


    /**
     * 代理服务创建
     */
    @RequestMapping("/create")
    public ResponseData create(@Validated(BaseEntity.Create.class) @RequestBody ProxyDto proxyDto) {
        int count = new QProxy().name.eq(proxyDto.getName()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PROXY_EXISTS);
        }
        return ResponseData.success(proxyService.create(proxyDto));
    }

    /**
     * 代理服务修改
     */
    @RequestMapping("/update")
    public ResponseData update(@Validated(BaseEntity.Update.class) @RequestBody ProxyDto proxyDto) {
        int count = new QProxy().name.eq(proxyDto.getName()).id.ne(proxyDto.getId()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PROXY_EXISTS);
        }
        return ResponseData.success(proxyService.update(proxyDto));
    }

    /**
     * 代理服务删除
     */
    @RequestMapping("/delete")
    public ResponseData delete(@Validated(BaseEntity.Delete.class) @RequestBody ProxyDto proxyDto) {
        proxyService.delete(proxyDto.getIds());
        return ResponseData.success(proxyDto.getIds());
    }

    /**
     * 监控信息
     */
    @RequestMapping("/monitor/info")
    public ResponseData monitorInfo() {

        return ResponseData.success(proxyService.monitorInfo());
    }
}
