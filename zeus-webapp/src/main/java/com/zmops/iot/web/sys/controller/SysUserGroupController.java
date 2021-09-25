package com.zmops.iot.web.sys.controller;

import com.zmops.iot.core.log.BussinessLog;
import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.auth.Permission;
import com.zmops.iot.web.sys.dto.UserGroupDto;
import com.zmops.iot.web.sys.dto.param.UserGroupParam;
import com.zmops.iot.web.sys.service.SysUserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yefei created at 2021/8/1 13:56
 * <p>
 * 用户组管理
 */
@RestController
@RequestMapping("/sys/userGroup")
public class SysUserGroupController {

    @Autowired
    SysUserGroupService sysUserGroupService;

    /**
     * 用户组分页列表
     *
     * @return
     */
    @Permission(code = "usrGrp")
    @PostMapping("/getUsrGrpByPage")
    public Pager<UserGroupDto> getUsrGrpByPage(@RequestBody UserGroupParam userGroupParam) {
        return sysUserGroupService.userGroupPageList(userGroupParam);
    }

    /**
     * 用户组列表
     *
     * @return
     */
    @Permission(code = "usrGrp")
    @PostMapping("/list")
    public ResponseData userGroupList() {
        return ResponseData.success(sysUserGroupService.userGroupList());
    }

    /**
     * 创建用户组
     *
     * @return
     */
    @Permission(code = "usrGrp")
    @PostMapping("/create")
    @BussinessLog(value = "创建用户组")
    public ResponseData createUserGroup(@Validated @RequestBody UserGroupDto userGroup) {
        return ResponseData.success(sysUserGroupService.createUserGroup(userGroup));
    }


    /**
     * 更新用户组
     *
     * @return
     */
    @Permission(code = "usrGrp")
    @PostMapping("/update")
    @BussinessLog(value = "更新用户组")
    public ResponseData updateUserGroup(@Validated(BaseEntity.Update.class) @RequestBody UserGroupDto userGroup) {
        return ResponseData.success(sysUserGroupService.updateUserGroup(userGroup));
    }

    /**
     * 删除用户组
     *
     * @return
     */
    @Permission(code = "usrGrp")
    @PostMapping("/delete")
    @BussinessLog(value = "删除用户组")
    public ResponseData deleteUserGroup(@Validated @RequestBody UserGroupParam userGroup) {
        sysUserGroupService.deleteUserGroup(userGroup);
        return ResponseData.success();
    }

    /**
     * 用户组绑定主机组
     *
     * @return
     */
    @Permission(code = "usrGrp")
    @PostMapping("/bindHostGrp")
    @BussinessLog(value = "用户组绑定主机组")
    public ResponseData bindHostGrp(@Validated(BaseEntity.BindDevice.class) @RequestBody UserGroupParam userGroup) {
        sysUserGroupService.bindHostGrp(userGroup);
        return ResponseData.success();
    }
}
