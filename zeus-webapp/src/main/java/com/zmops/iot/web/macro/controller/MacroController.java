package com.zmops.iot.web.macro.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.query.QProduct;
import com.zmops.iot.enums.InheritStatus;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.macro.dto.UserMacro;
import com.zmops.iot.web.macro.service.MacroService;
import com.zmops.zeus.driver.service.ZbxHost;
import com.zmops.zeus.driver.service.ZbxMacro;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author nantian created at 2021/9/18 10:53
 */

@RequestMapping("/macro")
@RestController
public class MacroController {

    @Autowired
    private ZbxMacro zbxMacro;

    @Autowired
    private ZbxHost zbxHost;

    @Autowired
    private MacroService macroService;

    /**
     * 创建 变量
     *
     * @return ResponseData
     */
    @RequestMapping("/create")
    public ResponseData createUserMacro(@Validated(BaseEntity.Create.class) @RequestBody UserMacro userMacro) {
        // 获取 host id
        String zbxId = new QDevice().select(QDevice.alias().zbxId).deviceId.eq(userMacro.getDeviceId()).findSingleAttribute();
        if (ToolUtil.isEmpty(zbxId)) {
            zbxId = new QProduct().select(QProduct.alias().zbxId).productId.eq(Long.parseLong(userMacro.getDeviceId())).findSingleAttribute();
        }

        if (ToolUtil.isEmpty(zbxId)) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_NOT_EXISTS);
        }

        return ResponseData.success(macroService.createMacro(userMacro, zbxId));
    }

    /**
     * 变量更新
     *
     * @param userMacro
     * @return ResponseData
     */
    @RequestMapping("/update")
    public ResponseData updateUserMacro(@Validated(BaseEntity.Update.class) @RequestBody UserMacro userMacro) {
        if (InheritStatus.YES.getCode().equals(userMacro.getInherit())) {
            String zbxId = new QDevice().select(QDevice.alias().zbxId).deviceId.eq(userMacro.getDeviceId()).findSingleAttribute();
            if (ToolUtil.isEmpty(zbxId)) {
                throw new ServiceException(BizExceptionEnum.PRODUCT_NOT_EXISTS);
            }
            macroService.createMacro(userMacro, zbxId);
        } else {
            zbxMacro.macroUpdate(userMacro.getHostmacroid(), userMacro.getMacro(), userMacro.getValue(), userMacro.getDescription());
        }
        return ResponseData.success();
    }

    /**
     * 变量列表
     *
     * @param userMacro
     * @return ResponseData
     */
    @RequestMapping("/list")
    public ResponseData getUserMacro(@Validated(BaseEntity.Get.class) @RequestBody UserMacro userMacro) {

        // 获取 模板ID 或者 设备ID
        String zbxId = "";
        Device device = new QDevice().deviceId.eq(userMacro.getDeviceId()).findOne();
        if (device != null) {
            zbxId = device.getZbxId();
        } else {
            zbxId = new QProduct().select(QProduct.alias().zbxId).productId.eq(Long.parseLong(userMacro.getDeviceId())).findSingleAttribute();
        }

        if (ToolUtil.isEmpty(zbxId)) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_NOT_EXISTS);
        }

        // 模板ID
        if (device == null) {
            List<UserMacro> tempMacroList = JSONObject.parseArray(zbxMacro.macroGet(zbxId + ""), UserMacro.class);
            return ResponseData.success(tempMacroList);
        }

        // 模板IDs
        List<HostQueryTempObject> tempListObject = JSON.parseArray(zbxHost.hostTempidGet(userMacro.getDeviceId()), HostQueryTempObject.class);

        TemplateIdObject tempId = tempListObject.get(0).getParentTemplates().get(0);
        if (ToolUtil.isNotEmpty(tempId)) {
            // 模板宏
            List<UserMacro> tempMacroList = JSONObject.parseArray(zbxMacro.macroGet(tempId.getTemplateid()), UserMacro.class);
            tempMacroList.forEach(macro -> {
                macro.setInherit("1");
                macro.setInheritName("否");
            });
            // 主机宏
            List<UserMacro> hostMacroList = JSONObject.parseArray(zbxMacro.macroGet(zbxId + ""), UserMacro.class);
            Map<String, UserMacro> hostMacroMap = hostMacroList.parallelStream().collect(Collectors.toMap(UserMacro::getMacro, o -> o, (a, b) -> a));
            //过滤模板宏中相同的KEY
            tempMacroList.removeIf(macro -> null != hostMacroMap.get(macro.getMacro()));

            tempMacroList.addAll(hostMacroList);

            return ResponseData.success(tempMacroList);
        }

        return ResponseData.error(ResponseData.DEFAULT_ERROR_CODE, "");
    }


    /**
     * 变量删除
     *
     * @param userMacro
     * @return ResponseData
     */
    @RequestMapping("/delete")
    public ResponseData deleteUserMacro(@Validated(BaseEntity.Delete.class) @RequestBody UserMacro userMacro) {
        zbxMacro.macroDelete(Collections.singletonList(userMacro.getHostmacroid()));
        return ResponseData.success();
    }


    @Getter
    @Setter
    static class HostQueryTempObject {
        private String                 hostid;
        private List<TemplateIdObject> parentTemplates;
    }

    @Getter
    @Setter
    static class TemplateIdObject {
        private String templateid;
        private String name;
    }

}
