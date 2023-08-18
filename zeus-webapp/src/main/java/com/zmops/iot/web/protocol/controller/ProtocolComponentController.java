package com.zmops.iot.web.protocol.controller;

import com.dtflys.forest.Forest;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.protocol.ProtocolComponent;
import com.zmops.iot.domain.protocol.query.QProtocolComponent;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.protocol.dto.ProtocolComponentDto;
import com.zmops.iot.web.protocol.dto.param.ProtocolComponentParam;
import com.zmops.iot.web.protocol.service.ProtocolComponentService;
import com.zmops.zeus.driver.service.ZeusServer;
import io.ebean.DB;
import io.ebean.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * 协议组件
 *
 * @author yefei
 **/
@RestController
@RequestMapping("/protocol/component")
public class ProtocolComponentController {

    @Autowired
    ProtocolComponentService protocolComponentService;

    @Autowired
    ZeusServer zeusServer;

    /**
     * 协议组件分页列表
     */
    @RequestMapping("/getProtocolComponentByPage")
    public Pager<ProtocolComponentDto> getProtocolComponentByPage(@RequestBody ProtocolComponentParam protocolComponentParam) {
        return protocolComponentService.getProtocolComponentByPage(protocolComponentParam);
    }

    /**
     * 协议组件列表
     */
    @RequestMapping("/list")
    public ResponseData list(@RequestBody ProtocolComponentParam protocolComponentParam) {
        return ResponseData.success(protocolComponentService.list(protocolComponentParam));
    }


    /**
     * 协议组件创建
     */
    @RequestMapping("/create")
    public ResponseData create(@Validated(BaseEntity.Create.class) @RequestBody ProtocolComponentParam protocolComponentParam) {
        int count = new QProtocolComponent().name.eq(protocolComponentParam.getName()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PROTOCOL_COMPONENT_EXISTS);
        }
        return ResponseData.success(protocolComponentService.create(protocolComponentParam));
    }

    /**
     * 协议组件修改
     */
    @RequestMapping("/update")
    public ResponseData update(@Validated(BaseEntity.Update.class) @RequestBody ProtocolComponentParam protocolComponentParam) {
        int count = new QProtocolComponent().name.eq(protocolComponentParam.getName()).protocolComponentId.ne(protocolComponentParam.getProtocolComponentId()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.PROTOCOL_COMPONENT_EXISTS);
        }
        return ResponseData.success(protocolComponentService.update(protocolComponentParam));
    }

    /**
     * 协议组件上传
     */
    @RequestMapping("/upload")
    public ResponseData upload(@RequestParam("file") MultipartFile file, @RequestParam("protocolComponentId") Long protocolComponentId, HttpServletRequest request) throws IOException, URISyntaxException {
        ProtocolComponent protocolComponent = new QProtocolComponent().protocolComponentId.eq(protocolComponentId).findOne();
        if (protocolComponent == null) {
            throw new ServiceException(BizExceptionEnum.PROTOCOL_COMPONENT_NOT_EXISTS);
        }
        try {
            zeusServer.upload("127.0.0.1", file, progress -> {
                System.out.println("total bytes: " + progress.getTotalBytes());   // 文件大小
                System.out.println("current bytes: " + progress.getCurrentBytes());   // 已上传字节数
                System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");  // 已上传百分比
                if (progress.isDone()) {   // 是否上传完成
                    System.out.println("--------   Upload Completed!   --------");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        protocolComponent.setStatus("1");
        protocolComponent.setFileName(file.getOriginalFilename());
        DB.update(protocolComponent);
        return ResponseData.success();
    }

    /**
     * 协议组件删除
     */
    @RequestMapping("/delete")
    public ResponseData delete(@Validated(BaseEntity.Delete.class) @RequestBody ProtocolComponentParam protocolComponentParam) {
        protocolComponentService.delete(protocolComponentParam.getProtocolComponentIds());
        return ResponseData.success(protocolComponentParam.getProtocolComponentIds());
    }

    /**
     * 协议组件发布
     */
    @RequestMapping("/publish")
    @Transactional
    public ResponseData publish(@RequestParam("protocolComponentId") Long protocolComponentId) {
        ProtocolComponent protocolComponent = new QProtocolComponent().protocolComponentId.eq(protocolComponentId).findOne();
        if (protocolComponent == null) {
            throw new ServiceException(BizExceptionEnum.PROTOCOL_COMPONENT_NOT_EXISTS);
        }
        protocolComponent.setStatus("2");
        DB.update(protocolComponent);
        //TODO 调用服务发布

        Map<String, String> params = new HashMap<>(1);
        params.put("protocolComponentId", protocolComponent.getProtocolComponentId() + "");
        params.put("fileName", protocolComponent.getFileName());
        Forest.post("/protocol/component/installArk").host("127.0.0.1").port(12800).addBody(params, "text/html;charset=utf-8").execute();

        return ResponseData.success();
    }

    /**
     * 协议组件取消发布
     */
    @RequestMapping("/unPublish")
    @Transactional
    public ResponseData unPublish(@RequestParam("protocolComponentId") Long protocolComponentId) {
        ProtocolComponent protocolComponent = new QProtocolComponent().protocolComponentId.eq(protocolComponentId).findOne();
        if (protocolComponent == null) {
            throw new ServiceException(BizExceptionEnum.PROTOCOL_COMPONENT_NOT_EXISTS);
        }
        protocolComponent.setStatus("1");
        DB.update(protocolComponent);

        Map<String, String> params = new HashMap<>(1);
        params.put("protocolComponentId", protocolComponent.getProtocolComponentId() + "");
        Forest.post("/protocol/component/uninstallArk").host("127.0.0.1").port(12800).addBody(params, "text/html;charset=utf-8").execute();

        return ResponseData.success();
    }
}
