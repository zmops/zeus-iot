package com.zmops.iot.web.sys.service;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.domain.device.DeviceGroup;
import com.zmops.iot.domain.device.DevicesGroups;
import com.zmops.iot.domain.device.query.QDeviceGroup;
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
import io.ebean.PagedList;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Pager<SysUserGroup> userGroupPageList(UserGroupParam userGroupParam) {

        QSysUserGroup qSysUserGroup = new QSysUserGroup();
        if (ToolUtil.isNotEmpty(userGroupParam.getGroupName())) {
            qSysUserGroup.groupName.contains(userGroupParam.getGroupName());
        }
        qSysUserGroup.setFirstRow((userGroupParam.getPage() - 1) * userGroupParam.getMaxRow()).setMaxRows(userGroupParam.getMaxRow());
        PagedList<SysUserGroup> pagedList = qSysUserGroup.findPagedList();
        return new Pager<>(pagedList.getList(), pagedList.getTotalCount());
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
        newUserGroup.setZbxId(Long.parseLong(userGrpids.get(0).toString()));
        DB.save(newUserGroup);

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

        List<Long> zbxUsrGrpIds = list.parallelStream().map(SysUserGroup::getZbxId).collect(Collectors.toList());
        zbxUserGroup.userGrpDelete(zbxUsrGrpIds);

        new QSysUserGroup().userGroupId.in(userGroupParam.getUserGroupIds()).delete();
    }

    /**
     * 根据用户组ID 取zabbix用户组ID
     */
    public Long getZabUsrGrpId(Long usrGrpId) {
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
        Long              usrGrpZbxId   = getZabUsrGrpId(userGroup.getUserGroupId());
        List<DeviceGroup> list          = new QDeviceGroup().deviceGroupId.in(userGroup.getDeviceGroupIds()).findList();
        List<Long>        hostGrpZbxIds = list.parallelStream().map(DeviceGroup::getZbxId).collect(Collectors.toList());
        zbxUserGroup.userGrpBindHostGroup(hostGrpZbxIds, usrGrpZbxId);
        List<DevicesGroups> lists = new ArrayList<>();
        for (Long deviceGroupId : userGroup.getDeviceGroupIds()) {
            DevicesGroups devicesGroups = new DevicesGroups();
            devicesGroups.setUserGroupId(userGroup.getUserGroupId());
            devicesGroups.setDeviceGroupId(deviceGroupId);
            lists.add(devicesGroups);
        }
        DB.saveAll(lists);
    }
}
