package com.zmops.iot.web.sys.service;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.device.DeviceGroup;
import com.zmops.iot.domain.device.SysUserGrpDevGrp;
import com.zmops.iot.domain.device.query.QDeviceGroup;
import com.zmops.iot.domain.device.query.QSysUserGrpDevGrp;
import com.zmops.iot.domain.sys.SysUserGroup;
import com.zmops.iot.domain.sys.query.QSysUser;
import com.zmops.iot.domain.sys.query.QSysUserGroup;
import com.zmops.iot.enums.CommonStatus;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.sys.dto.UserGroupDto;
import com.zmops.iot.web.sys.dto.param.UserGroupParam;
import com.zmops.zeus.driver.service.ZbxUserGroup;
import io.ebean.DB;
import io.ebean.DtoQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yefei
 **/
@Service
public class SysUserGroupService {

    @Autowired
    private ZbxUserGroup zbxUserGroup;


    /**
     * 用户组分页列表
     *
     * @param userGroupParam
     * @return
     */
    public Pager<UserGroupDto> userGroupPageList(UserGroupParam userGroupParam) {
        QSysUserGroup qSysUserGroup = new QSysUserGroup();
        StringBuilder sql = new StringBuilder("SELECT " +
                " sug.group_name, sug.remark, sug.create_time, sug.create_user, sug.update_time, sug.update_user, sug.status, sug.user_group_id, " +
                " dg.groupIds  " +
                "FROM " +
                " sys_user_group sug " +
                " LEFT JOIN ( SELECT user_group_id, array_to_string( ARRAY_AGG ( device_group_id ), ',' ) groupIds FROM sys_usrgrp_devicegrp GROUP BY user_group_id ) dg " +
                " ON dg.user_group_id = sug.user_group_id");
        if (ToolUtil.isNotEmpty(userGroupParam.getGroupName())) {
            sql.append(" where sug.group_name like  :groupName");
        }
        sql.append(" order by sug.create_time desc ");
        DtoQuery<UserGroupDto> dto = DB.findDto(UserGroupDto.class, sql.toString());
        if (ToolUtil.isNotEmpty(userGroupParam.getGroupName())) {
            dto.setParameter("groupName", "%" + userGroupParam.getGroupName() + "%");
            qSysUserGroup.groupName.contains(userGroupParam.getGroupName());
        }
        List<UserGroupDto> list = dto.setFirstRow((userGroupParam.getPage() - 1) * userGroupParam.getMaxRow())
                .setMaxRows(userGroupParam.getMaxRow()).findList();
        return new Pager<>(list, qSysUserGroup.findCount());
    }

    /**
     * 用户组列表
     *
     * @return
     */
    public List<SysUserGroup> userGroupList() {
//        QSysUserGroup qSysUserGroup = new QSysUserGroup();
//        if (ToolUtil.isNotEmpty(userGroupParam.getGroupName())) {
//            qSysUserGroup.groupName.contains(userGroupParam.getGroupName());
//        }

        return new QSysUserGroup().findList();
    }

    /**
     * 添加用戶組
     */
    @Transactional(rollbackFor = Exception.class)
    public SysUserGroup createUserGroup(UserGroupDto userGroup) {
        // 判断用户组是否重复
        checkByGroupName(userGroup.getGroupName(), -1L);
        long usrGrpId = IdUtil.getSnowflake().nextId();

        SysUserGroup newUserGroup = new SysUserGroup();
        BeanUtils.copyProperties(userGroup, newUserGroup);
        newUserGroup.setUserGroupId(usrGrpId);
        newUserGroup.setStatus(CommonStatus.ENABLE.getCode());
        //回填 ZBX用户组ID
        JSONObject result     = JSONObject.parseObject(zbxUserGroup.userGrpAdd(String.valueOf(usrGrpId)));
        JSONArray  userGrpids = result.getJSONArray("usrgrpids");
        newUserGroup.setZbxId(userGrpids.get(0).toString());
        DB.save(newUserGroup);

        if (ToolUtil.isNotEmpty(userGroup.getDeviceGroupIds())) {
            bindHostGrp(UserGroupParam.builder().userGroupId(usrGrpId).deviceGroupIds(userGroup.getDeviceGroupIds()).build());
        }

        return newUserGroup;
    }

    /**
     * 修改用户组
     *
     * @param userGroup
     * @return SysUser
     */
    public SysUserGroup updateUserGroup(UserGroupDto userGroup) {
        // 判断用户组是否重复
        checkByGroupName(userGroup.getGroupName(), userGroup.getUserGroupId());

        SysUserGroup newUserGroup = new SysUserGroup();
        BeanUtils.copyProperties(userGroup, newUserGroup);
        DB.update(newUserGroup);

        if (ToolUtil.isNotEmpty(userGroup.getDeviceGroupIds())) {
            bindHostGrp(UserGroupParam.builder().userGroupId(userGroup.getUserGroupId()).deviceGroupIds(userGroup.getDeviceGroupIds()).build());
        }

        return newUserGroup;
    }


    /**
     * 根据GroupName userGroupId检查用户组是否已存在
     *
     * @param groupName
     */
    private void checkByGroupName(String groupName, Long userGroupId) {
        int count;
        if (userGroupId > 0) {
            count = new QSysUserGroup().groupName.equalTo(groupName).userGroupId.ne(userGroupId).findCount();
        } else {
            count = new QSysUserGroup().groupName.equalTo(groupName).findCount();
        }
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.USERGROUP_HAS_EXIST);
        }
    }

    /**
     * 删除用户组
     *
     * @param userGroupParam
     */
    public void deleteUserGroup(UserGroupParam userGroupParam) {
        List<SysUserGroup> list = new QSysUserGroup().userGroupId.in(userGroupParam.getUserGroupIds()).findList();
        if (ToolUtil.isEmpty(list)) {
            throw new ServiceException(BizExceptionEnum.USERGROUP_NOT_EXIST);
        }

        int count = new QSysUser().userGroupId.in(userGroupParam.getUserGroupIds()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.USERGROUP_HAS_BIND_USER);
        }

        List<String> zbxUsrGrpIds = list.parallelStream().map(SysUserGroup::getZbxId).collect(Collectors.toList());
        zbxUserGroup.userGrpDelete(zbxUsrGrpIds);

        // 删除 与设备组关联
        new QSysUserGrpDevGrp().userGroupId.in(userGroupParam.getUserGroupIds()).delete();
        new QSysUserGroup().userGroupId.in(userGroupParam.getUserGroupIds()).delete();
    }

    /**
     * 根据用户组ID 取zabbix用户组ID
     */
    public String getZabUsrGrpId(Long usrGrpId) {
        SysUserGroup usrGrp = new QSysUserGroup().userGroupId.eq(usrGrpId).findOne();
        if (null == usrGrp) {
            throw new ServiceException(BizExceptionEnum.USERGROUP_NOT_EXIST);
        }
        return usrGrp.getZbxId();
    }

    /**
     * 用户组绑定设备组
     *
     * @param userGroup
     */
    public void bindHostGrp(UserGroupParam userGroup) {
        //修改ZBX 用户组绑定主机组
        String            usrGrpZbxId   = getZabUsrGrpId(userGroup.getUserGroupId());
        List<DeviceGroup> list          = new QDeviceGroup().deviceGroupId.in(userGroup.getDeviceGroupIds()).findList();
        List<String>      hostGrpZbxIds = list.parallelStream().map(DeviceGroup::getZbxId).collect(Collectors.toList());
        zbxUserGroup.userGrpBindHostGroup(hostGrpZbxIds, usrGrpZbxId);
        List<SysUserGrpDevGrp> lists = new ArrayList<>();
        for (Long deviceGroupId : userGroup.getDeviceGroupIds()) {
            SysUserGrpDevGrp devicesGroups = new SysUserGrpDevGrp();
            devicesGroups.setUserGroupId(userGroup.getUserGroupId());
            devicesGroups.setDeviceGroupId(deviceGroupId);
            lists.add(devicesGroups);
        }
        DB.saveAll(lists);
    }
}
