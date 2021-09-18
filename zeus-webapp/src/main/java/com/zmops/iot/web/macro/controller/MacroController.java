package com.zmops.iot.web.macro.controller;

import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.domain.product.query.QProduct;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.macro.dto.UserMacro;
import com.zmops.zeus.driver.service.ZbxMacro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author nantian created at 2021/9/18 10:53
 */

@RequestMapping("/macro")
@RestController
public class MacroController {

    @Autowired
    ZbxMacro zbxMacro;

    /**
     * 创建宏
     *
     * @return
     */
    @RequestMapping("/create")
    public ResponseData createUserMacro(@Validated(BaseEntity.Create.class) @RequestBody UserMacro userMacro) {
        String zbxId = new QProduct().select(QProduct.alias().zbxId).productId.eq(Long.parseLong(userMacro.getDeviceId())).findSingleAttribute();
        if (ToolUtil.isEmpty(zbxId)) {
            zbxId = new QDevice().select(QDevice.alias().zbxId).deviceId.eq(userMacro.getDeviceId()).findSingleAttribute();
        }
        if (ToolUtil.isEmpty(zbxId)) {
            throw new ServiceException(BizExceptionEnum.PRODUCT_NOT_EXISTS);
        }
        zbxMacro.macroCreate(zbxId, userMacro.getMacro(), userMacro.getValue(), userMacro.getDescription());
        return ResponseData.success();
    }


    @RequestMapping("/update")
    public ResponseData updateUserMacro(@Validated(BaseEntity.Update.class) @RequestBody UserMacro userMacro) {
        zbxMacro.macroUpdate(userMacro.getHostMacroId(), userMacro.getMacro(), userMacro.getValue(), userMacro.getDescription());
        return ResponseData.success();
    }


    @RequestMapping("/list")
    public ResponseData getUserMacro(@Validated(BaseEntity.Get.class) @RequestBody UserMacro userMacro) {
        String zbxId = new QProduct().select(QProduct.alias().zbxId).productId.eq(Long.parseLong(userMacro.getDeviceId())).findSingleAttribute();
        if (ToolUtil.isEmpty(zbxId)) {
            zbxId = new QDevice().select(QDevice.alias().zbxId).deviceId.eq(userMacro.getDeviceId()).findSingleAttribute();
        }
        List<UserMacro> userMacroList = new ArrayList<>();
        if (ToolUtil.isNotEmpty(zbxId)) {
            userMacroList = JSONObject.parseArray(zbxMacro.macroQuery(zbxId, userMacro.getMacro()), UserMacro.class);
        }
        return ResponseData.success(userMacroList);
    }


    @RequestMapping("/delete")
    public ResponseData deleteUserMacro(@Validated(BaseEntity.Delete.class) @RequestBody UserMacro userMacro) {
        zbxMacro.macroDelete(Collections.singletonList(userMacro.getHostMacroId()));
        return ResponseData.success();
    }

}
