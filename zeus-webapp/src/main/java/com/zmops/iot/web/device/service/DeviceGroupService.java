package com.zmops.iot.web.device.service;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zmops.iot.core.auth.context.LoginContextHolder;
import com.zmops.iot.core.auth.model.LoginUser;
import com.zmops.iot.domain.device.DeviceGroup;
import com.zmops.iot.domain.device.query.QDeviceGroup;
import com.zmops.iot.domain.device.query.QDevicesGroups;
import com.zmops.iot.domain.device.query.QSysUserGrpDevGrp;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.DeviceGroupDto;
import com.zmops.iot.web.device.dto.param.DeviceGroupParam;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.zeus.driver.entity.ZbxHostGrpInfo;
import com.zmops.zeus.driver.service.ZbxHostGroup;
import io.ebean.DB;
import io.ebean.PagedList;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yefei
 **/
@Service
public class DeviceGroupService {

    @Autowired
    private ZbxHostGroup zbxHostGroup;


    /**
     * 设备组分页列表
     *
     * @param devGroupParam
     * @return
     */
    public Pager<DeviceGroupDto> deviceGroupPageList(DeviceGroupParam devGroupParam) {
        List<Long> devGroupIds = getDevGroupIds();
        if (ToolUtil.isEmpty(devGroupIds)) {
            return new Pager<>();
        }

        QDeviceGroup qDeviceGroup = new QDeviceGroup();

        qDeviceGroup.deviceGroupId.in(devGroupIds);

        if (ToolUtil.isNotEmpty(devGroupParam.getName())) {
            qDeviceGroup.name.contains(devGroupParam.getName());
        }
        qDeviceGroup.order().createTime.desc()
                .setFirstRow((devGroupParam.getPage() - 1) * devGroupParam.getMaxRow())
                .setMaxRows(devGroupParam.getMaxRow());

        PagedList<DeviceGroup> pagedList = qDeviceGroup.orderBy(" create_time desc").findPagedList();

        List<DeviceGroupDto> deviceGroupDtos = ToolUtil.convertBean(pagedList.getList(), DeviceGroupDto.class);
        return new Pager<>(deviceGroupDtos, pagedList.getTotalCount());
    }

    /**
     * 设备组列表
     *
     * @param devGroupParam
     * @return
     */
    public List<DeviceGroup> deviceGroupList(DeviceGroupParam devGroupParam) {
        List<Long> devGroupIds = getDevGroupIds();
        if (ToolUtil.isEmpty(devGroupIds)) {
            return Collections.emptyList();
        }
        QDeviceGroup qDeviceGroup = new QDeviceGroup();
        qDeviceGroup.deviceGroupId.in(devGroupIds);
        if (ToolUtil.isNotEmpty(devGroupParam.getName())) {
            qDeviceGroup.name.contains(devGroupParam.getName());
        }

        return qDeviceGroup.findList();
    }

    /**
     * 获取当前登录用户绑定的主机组ID
     *
     * @return
     */
    public List<Long> getDevGroupIds() {
        LoginUser user         = LoginContextHolder.getContext().getUser();
        Long      curUserGrpId = user.getUserGroupId();
        Long      tenantId     = user.getTenantId();

        List<Long> devGroupIds = new ArrayList<>();
        if (null != curUserGrpId) {
            devGroupIds = new QSysUserGrpDevGrp().select(QSysUserGrpDevGrp.alias().deviceGroupId).userGroupId.eq(curUserGrpId).findSingleAttributeList();
        }

        if (ToolUtil.isEmpty(devGroupIds) && null != curUserGrpId) {
            QDeviceGroup qDeviceGroup = new QDeviceGroup().select(QDeviceGroup.alias().deviceGroupId);
            if (null != tenantId) {
                qDeviceGroup.tenantId.eq(tenantId);
            }
            devGroupIds = qDeviceGroup.findSingleAttributeList();
        }
        return devGroupIds;
    }

    /**
     * 添加设备組
     */
    public DeviceGroup createDeviceGroup(DeviceGroupParam deviceGroup) {
        // 判断设备组是否重复
        checkByGroupName(deviceGroup.getName(), -1L, deviceGroup.getTenantId());
        //获取ID
        long devGrpId = IdUtil.getSnowflake().nextId();
        // 先在ZBX 添加主机组

        DeviceGroup newDeviceGroup = new DeviceGroup();
        BeanUtils.copyProperties(deviceGroup, newDeviceGroup);
        newDeviceGroup.setDeviceGroupId(devGrpId);
        //回填 ZBX主机组ID
        JSONObject result = JSONObject.parseObject(zbxHostGroup.hostGroupCreate(String.valueOf(devGrpId)));
        JSONArray grpids = result.getJSONArray("groupids");
        newDeviceGroup.setZbxId(grpids.get(0).toString());
        DB.save(newDeviceGroup);
        return newDeviceGroup;
    }

    /**
     * 修改设备组
     *
     * @param deviceGroup
     * @return DeviceGroup
     */
    public DeviceGroup updateDeviceGroup(DeviceGroupParam deviceGroup) {
        // 判断设备组是否重复
        checkByGroupName(deviceGroup.getName(), deviceGroup.getDeviceGroupId(), deviceGroup.getTenantId());

        DeviceGroup newDeviceGroup = new DeviceGroup();
        BeanUtils.copyProperties(deviceGroup, newDeviceGroup);
        DB.update(newDeviceGroup);
        return newDeviceGroup;
    }


    /**
     * 根据GroupName userGroupId检查用户组是否已存在
     *
     * @param groupName
     */
    private void checkByGroupName(String groupName, long deviceGroupId, Long tenantId) {
        int count;
        if (deviceGroupId >= 0) {
            count = new QDeviceGroup().name.equalTo(groupName).tenantId.eq(tenantId).deviceGroupId.ne(deviceGroupId).findCount();
        } else {
            count = new QDeviceGroup().name.equalTo(groupName).tenantId.eq(tenantId).findCount();
        }
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.DEVICEGROUP_HAS_EXIST);
        }
    }

    /**
     * 删除设备组
     *
     * @param deviceGroupParam
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDeviceGroup(DeviceGroupParam deviceGroupParam) {
        List<DeviceGroup> list = new QDeviceGroup().deviceGroupId.in(deviceGroupParam.getDeviceGroupIds()).findList();
        if (ToolUtil.isEmpty(list)) {
            throw new ServiceException(BizExceptionEnum.DEVICEGROUP_NOT_EXIST);
        }

        //检查是否关联设备
        int count = new QDevicesGroups().deviceGroupId.in(deviceGroupParam.getDeviceGroupIds()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.DEVICEGROUP_HAS_BIND_DEVICE);
        }

        //删除ZBX中主机组
        List<String> zbxHostGrpIds = list.parallelStream().map(DeviceGroup::getZbxId).collect(Collectors.toList());
        if (ToolUtil.isNotEmpty(zbxHostGrpIds)) {
            List<ZbxHostGrpInfo> zbxHostGrpInfos = JSONObject.parseArray(zbxHostGroup.getHostGroup(zbxHostGrpIds.toString()), ZbxHostGrpInfo.class);
            if (ToolUtil.isNotEmpty(zbxHostGrpInfos)) {
                zbxHostGroup.hostGroupDelete(zbxHostGrpInfos.parallelStream().map(ZbxHostGrpInfo::getGroupid).collect(Collectors.toList()));
            }
        }


        // 删除 与用户组关联
        new QSysUserGrpDevGrp().deviceGroupId.in(deviceGroupParam.getDeviceGroupIds()).delete();
        new QDeviceGroup().deviceGroupId.in(deviceGroupParam.getDeviceGroupIds()).delete();
    }

}
