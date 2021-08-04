package com.zmops.iot.web.sys.controller;

import com.zmops.iot.core.log.BussinessLog;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.sys.SysUser;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.sys.dto.UserDto;
import com.zmops.iot.web.sys.dto.param.UserParam;
import com.zmops.iot.web.sys.service.SysUserService;
import io.ebean.SqlRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author nantian created at 2021/8/1 21:56
 * <p>
 * 用户管理
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    @Autowired
    SysUserService sysUserService;

    /**
     * 用户列表
     *
     * @return
     */
    @RequestMapping("/list")
    public Pager<UserDto> userList(@RequestBody UserParam userParam) {
        return sysUserService.userList(userParam);
    }

    /**
     * 创建用户
     *
     * @return
     */
    @PostMapping("/create")
    @BussinessLog(value = "创建用户")
    public ResponseData createUser(@RequestBody UserDto sysUser) {
        return ResponseData.success(sysUserService.createUser(sysUser));
    }


    /**
     * 更新用户
     *
     * @return
     */
    @PostMapping("/update")
    @BussinessLog(value = "更新用户")
    public ResponseData updateUser(@Validated(value = BaseEntity.Update.class) @RequestBody UserDto userDto) {
        return ResponseData.success(sysUserService.updateUser(userDto));
    }

    /**
     * 删除用户
     *
     * @return
     */
    @PostMapping("/delete")
    @BussinessLog(value = "删除用户")
    public ResponseData deleteUser(@Valid @RequestBody UserParam user) {
            sysUserService.deleteUser(user);
            return ResponseData.success();
    }
}
