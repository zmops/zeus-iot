package com.zmops.iot.web.macro.service;

import com.alibaba.fastjson.JSON;
import com.zmops.iot.web.macro.dto.UserMacro;
import com.zmops.zeus.driver.service.ZbxMacro;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * @author yefei
 **/
@Service
public class MacroService {

    @Autowired
    ZbxMacro zbxMacro;

    public Long createMacro(UserMacro userMacro, String zbxId) {

        // 创建宏
        String res = zbxMacro.macroCreate(zbxId, userMacro.getMacro().toUpperCase(Locale.ROOT), userMacro.getValue(), userMacro.getDescription());

        Macroids macroids = JSON.parseObject(res, Macroids.class);
        return macroids.getHostmacroids()[0];
    }

    @Getter
    @Setter
    static class Macroids {
        private Long[] hostmacroids;
    }
}
