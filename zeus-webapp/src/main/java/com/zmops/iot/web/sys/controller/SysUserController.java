package com.zmops.iot.web.sys.controller;

import com.zmops.iot.core.log.BussinessLog;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.auth.Permission;
import com.zmops.iot.web.sys.dto.UserDto;
import com.zmops.iot.web.sys.dto.param.UserParam;
import com.zmops.iot.web.sys.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @Permission(code = "mgr")
    @PostMapping("/getUserByPage")
    public Pager<UserDto> userList(@RequestBody UserParam userParam) {
        return sysUserService.userList(userParam);
    }

    /**
     * 创建用户
     *
     * @return
     */
    @Permission(code = "mgr")
    @PostMapping("/create")
    @BussinessLog(value = "创建用户")
    public ResponseData createUser(@Validated(BaseEntity.Create.class) @RequestBody UserDto sysUser) {
        return ResponseData.success(sysUserService.createUser(sysUser));
    }


    /**
     * 更新用户
     *
     * @return
     */
    @Permission(code = "mgr")
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
    @Permission(code = "mgr")
    @PostMapping("/delete")
    @BussinessLog(value = "删除用户")
    public ResponseData deleteUser(@Validated(BaseEntity.Delete.class) @RequestBody UserParam user) {
        sysUserService.deleteUser(user);
        return ResponseData.success();
    }

    /**
     * 修改密码
     */
    @Permission(code = "mgr")
    @PostMapping("/changePwd")
    @BussinessLog(value = "修改密码")
    public ResponseData changePwd(@Valid @RequestBody UserParam user) {
        sysUserService.changePwd(user.getOldPassword(), user.getNewPassword());
        return ResponseData.success();
    }

    /**
     * 重置管理员的密码
     */
    @Permission(code = "mgr")
    @RequestMapping("/reset")
    @BussinessLog(value = "重置密码")
    public ResponseData reset(@RequestParam("userId") Long userId) {
        sysUserService.reset(userId);
        return ResponseData.success();
    }
}
