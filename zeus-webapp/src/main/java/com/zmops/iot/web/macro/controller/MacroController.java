package com.zmops.iot.web.macro.controller;

import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.macro.dto.UserMacro;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author nantian created at 2021/9/18 10:53
 */

@RequestMapping("/macro")
public class MacroController {

    /**
     * 创建宏
     *
     * @return
     */
    @RequestMapping("/create")
    public ResponseData createUserMacro(@RequestBody UserMacro userMacro) {


        return ResponseData.success();
    }


    @RequestMapping("/update")
    public ResponseData updateUserMacro(@RequestBody UserMacro userMacro) {

        return ResponseData.success();
    }


    @RequestMapping("/get")
    public ResponseData getUserMacro(@RequestBody UserMacro userMacro) {

        return ResponseData.success();
    }


    @RequestMapping("/delete")
    public ResponseData deleteUserMacro(@RequestBody UserMacro userMacro) {

        return ResponseData.success();
    }

}
