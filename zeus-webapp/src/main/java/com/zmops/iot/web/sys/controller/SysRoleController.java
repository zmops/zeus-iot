package com.zmops.iot.web.sys.controller;

import com.zmops.iot.core.log.BussinessLog;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.auth.Permission;
import com.zmops.iot.web.sys.dto.SysRoleDto;
import com.zmops.iot.web.sys.dto.param.RoleParam;
import com.zmops.iot.web.sys.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nantian created at 2021/8/1 21:57
 * <p>
 * 角色管理
 */


@RestController
@RequestMapping("/sys/role")
public class SysRoleController {

    @Autowired
    SysRoleService sysRoleService;

    /**
     * 角色列表
     */
    @RequestMapping("/list")
    public ResponseData list(@RequestParam(value = "name", required = false) String name) {
        return ResponseData.success(sysRoleService.list(name));
    }

    /**
     * 角色新增
     */
    @Permission(code = "role")
    @RequestMapping("/create")
    @BussinessLog(value = "角色新增")
    public ResponseData create(@Validated(BaseEntity.Create.class) @RequestBody SysRoleDto sysRoleDto) {
        return ResponseData.success(sysRoleService.create(sysRoleDto));
    }

    /**
     * 角色修改
     */
    @Permission(code = "role")
    @RequestMapping("/update")
    @BussinessLog(value = "角色修改")
    public ResponseData update(@Validated(BaseEntity.Update.class) @RequestBody SysRoleDto sysRoleDto) {
        return ResponseData.success(sysRoleService.update(sysRoleDto));
    }

    /**
     * 角色删除
     */
    @Permission(code = "role")
    @RequestMapping("/delete")
    @BussinessLog(value = "角色删除")
    public ResponseData delete(@Validated(BaseEntity.Delete.class) @RequestBody RoleParam sysRoleParam) {
        sysRoleService.delete(sysRoleParam);
        return ResponseData.success();
    }

    /**
     * 绑定菜单
     *
     * @return
     */
    @Permission(code = "role")
    @RequestMapping("/bindMenu")
    @BussinessLog(value = "角色绑定菜单")
    public ResponseData bindMenu(@Validated @RequestBody RoleParam roleParam) {
        sysRoleService.bindMenu(roleParam);
        return ResponseData.success();
    }

    /**
     * 角色已绑定的菜单
     */
    @Permission(code = "role")
    @RequestMapping("/bindedMenu")
    public ResponseData bindMenu(@RequestParam(value = "roleId") Long roleId) {
        return ResponseData.success(sysRoleService.bindedMenu(roleId));
    }
}
