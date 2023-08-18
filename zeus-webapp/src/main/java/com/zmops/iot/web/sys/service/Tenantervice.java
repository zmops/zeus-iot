package com.zmops.iot.web.sys.service;

import com.zmops.iot.constant.ConstantsContext;
import com.zmops.iot.domain.device.DeviceGroup;
import com.zmops.iot.domain.device.query.QDeviceGroup;
import com.zmops.iot.domain.product.query.QProductType;
import com.zmops.iot.domain.sys.SysUser;
import com.zmops.iot.domain.sys.SysUserGroup;
import com.zmops.iot.domain.sys.query.QSysUser;
import com.zmops.iot.domain.sys.query.QSysUserGroup;
import com.zmops.iot.domain.tenant.TenantInfo;
import com.zmops.iot.domain.tenant.query.QTenantInfo;
import com.zmops.iot.enums.CommonStatus;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.util.DefinitionsUtil;
import com.zmops.iot.util.ToolUtil;
import com.zmops.iot.web.device.dto.param.DeviceGroupParam;
import com.zmops.iot.web.device.service.DeviceGroupService;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.product.dto.param.ProductTypeParam;
import com.zmops.iot.web.product.service.ProductTypeService;
import com.zmops.iot.web.sys.dto.TenantInfoDto;
import com.zmops.iot.web.sys.dto.UserDto;
import com.zmops.iot.web.sys.dto.UserGroupDto;
import com.zmops.iot.web.sys.dto.param.TenantInfoParam;
import io.ebean.DB;
import io.ebean.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @author yefei
 **/
@Service
public class Tenantervice implements CommandLineRunner {

    private static final String ZEUS_TENANT_ROLE_ID = "ZEUS_TENANT_ROLE_ID";

    @Autowired
    SysUserGroupService sysUserGroupService;

    @Autowired
    DeviceGroupService deviceGroupService;

    @Autowired
    SysUserService sysUserService;

    @Autowired
    ProductTypeService productTypeService;

    public Pager<TenantInfoDto> getTenantByPage(TenantInfoParam tenantInfoParam) {
        QTenantInfo qTenantInfo = new QTenantInfo();
        if (ToolUtil.isNotEmpty(tenantInfoParam.getName())) {
            qTenantInfo.name.contains(tenantInfoParam.getName());
        }
        List<TenantInfoDto> list = qTenantInfo.orderBy().createTime.desc().asDto(TenantInfoDto.class).setFirstRow((tenantInfoParam.getPage() - 1) * tenantInfoParam.getMaxRow())
                .setMaxRows(tenantInfoParam.getMaxRow()).findList();
        list.forEach(tenantInfoDto -> {
            int count = new QSysUser().tenantId.eq(tenantInfoDto.getTenantId()).findCount();
            tenantInfoDto.setUserNum(count);
        });
        return new Pager<>(list, qTenantInfo.findCount());
    }

    @Transactional
    public Object createTenant(TenantInfoParam tenantInfoParam) {
        //step 1:新增租户
        TenantInfo tenantInfo = new TenantInfo();
        ToolUtil.copyProperties(tenantInfoParam, tenantInfo);
        tenantInfo.setStatus(CommonStatus.ENABLE.getCode());
        DB.save(tenantInfo);

        //step 2: 新增默认产品分类
        addProductType(tenantInfo.getTenantId(), tenantInfoParam.getName());
        //step 3:新增默认设备组
        addDeviceGrp(tenantInfo.getTenantId(), tenantInfoParam.getName());
        //step 4:新增租户管理员账号
        long userGrpId = addSysUserGrp(tenantInfo.getTenantId(), tenantInfoParam.getName());
        addSysUser(tenantInfoParam, userGrpId, tenantInfo.getTenantId());
        //step 5:新增租户告警通知方式设置
        addMediaTypeSetting(tenantInfo.getTenantId());
        //更新租户名称缓存
        updateTenantName();
        return tenantInfo;
    }

    private void addMediaTypeSetting(Long tenantId) {
        DB.sqlUpdate("insert into media_type_setting (type,template,webhooks,tenant_id) SELECT type,template,webhooks,:tenantId from media_type_setting where tenant_id is null")
                .setParameter("tenantId", tenantId).execute();

        DB.sqlUpdate("insert into mail_setting (host, port, account, password, sender, ssl, tls, severity, silent,  tenant_id) SELECT host, port, account, password, sender, ssl, tls, severity, silent,:tenantId from mail_setting where tenant_id is null")
                .setParameter("tenantId", tenantId).execute();
    }

    private void addProductType(Long tenantId, String tenantName) {
        ProductTypeParam productTypeParam = new ProductTypeParam();
        productTypeParam.setName("默认产品分组-" + tenantName);
        productTypeParam.setTenantId(tenantId);
        productTypeService.create(productTypeParam);
    }

    private long addDeviceGrp(Long tenantId, String tenantName) {
        DeviceGroupParam deviceGroupDto = new DeviceGroupParam();
        deviceGroupDto.setName("默认设备组-" + tenantName);
        deviceGroupDto.setTenantId(tenantId);
        DeviceGroup deviceGroup = deviceGroupService.createDeviceGroup(deviceGroupDto);
        return deviceGroup.getDeviceGroupId();
    }

    private long addSysUserGrp(Long tenantId, String tenantName) {
        UserGroupDto userGroupDto = new UserGroupDto();
        userGroupDto.setGroupName("管理员组-" + tenantName);
        userGroupDto.setRemark("默认管理员组，不要绑定设备组");
        userGroupDto.setTenantId(tenantId);
        SysUserGroup userGroup = sysUserGroupService.createUserGroup(userGroupDto);
        return userGroup.getUserGroupId();
    }

    private void addSysUser(TenantInfoParam tenantInfoParam, Long userGrpId, Long tenantId) {
        UserDto userDto = new UserDto();
        userDto.setAccount(tenantInfoParam.getAccount());
        userDto.setPassword(tenantInfoParam.getPassword());
        userDto.setRoleId(Long.parseLong(ConstantsContext.getConstntsMap().getOrDefault(ZEUS_TENANT_ROLE_ID, "1").toString()));
        userDto.setUserGroupId(userGrpId);
        String userName = tenantInfoParam.getContact();
        if (ToolUtil.isEmpty(userName)) {
            userName = tenantInfoParam.getName() + "管理员";
        }
        userDto.setName(userName);
        userDto.setPhone(tenantInfoParam.getPhone());
        userDto.setTenantId(tenantId);
        sysUserService.createUser(userDto);
    }

    @Transactional
    public Object updateTenant(TenantInfoParam tenantInfoParam) {
        TenantInfo tenantInfo = new QTenantInfo().tenantId.eq(tenantInfoParam.getTenantId()).findOne();
        if (null == tenantInfo) {
            throw new ServiceException(BizExceptionEnum.TENANT_NOT_EXISTS);
        }
        tenantInfo.setName(tenantInfoParam.getName());
        tenantInfo.setRemark(tenantInfoParam.getRemark());
        tenantInfo.setContact(tenantInfoParam.getContact());
        tenantInfo.setPhone(tenantInfoParam.getPhone());
        DB.update(tenantInfo);
        updateTenantName();
        return tenantInfo;
    }

    public void deleteTenant(TenantInfoParam tenantInfoParam) {
        if (isRelationInfo(tenantInfoParam.getTenantId())) {
            throw new ServiceException(BizExceptionEnum.TENANT_HAS_RELATION_INFO);
        }
        new QTenantInfo().tenantId.eq(tenantInfoParam.getTenantId()).delete();
        updateTenantName();
    }

    private boolean isRelationInfo(Long tenantId) {

        int count = new QSysUserGroup().tenantId.eq(tenantId).findCount();
        if (count > 0) {
            return true;
        }
        count = new QDeviceGroup().tenantId.eq(tenantId).findCount();
        if (count > 0) {
            return true;
        }
        count = new QProductType().tenantId.eq(tenantId).findCount();
        if (count > 0) {
            return true;
        }

        return false;
    }

    /**
     * 启用 禁用租户
     *
     * @param tenantId
     * @param status
     */
    @Transactional(rollbackFor = Exception.class)
    public void status(Long tenantId, String status) {
        DB.update(SysUser.class).where().eq("tenantId", tenantId).asUpdate().set("status", status).update();
        DB.update(TenantInfo.class).where().eq("tenantId", tenantId).asUpdate().set("status", status).update();
    }

    private void updateTenantName() {
        List<TenantInfo> list = new QTenantInfo().findList();
        if (ToolUtil.isEmpty(list)) {
            return;
        }
        DefinitionsUtil.updateTenantName(list.parallelStream().collect(Collectors.toMap(TenantInfo::getTenantId, TenantInfo::getName, (a, b) -> a)));
    }

    @Override
    public void run(String... args) throws Exception {
        updateTenantName();
    }


}
