package com.zmops.iot.web.sys.controller;

import com.zmops.iot.domain.BaseEntity;
import com.zmops.iot.domain.sys.SysUser;
import com.zmops.iot.domain.sys.query.QSysUser;
import com.zmops.iot.domain.tenant.TenantInfo;
import com.zmops.iot.domain.tenant.query.QTenantInfo;
import com.zmops.iot.model.exception.ServiceException;
import com.zmops.iot.model.page.Pager;
import com.zmops.iot.model.response.ResponseData;
import com.zmops.iot.web.auth.Permission;
import com.zmops.iot.web.exception.enums.BizExceptionEnum;
import com.zmops.iot.web.sys.dto.TenantInfoDto;
import com.zmops.iot.web.sys.dto.param.TenantInfoParam;
import com.zmops.iot.web.sys.service.SysUserService;
import com.zmops.iot.web.sys.service.Tenantervice;
import io.ebean.DB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author yefei
 **/
@RestController
@RequestMapping("/tenant")
public class TenantController {

    @Autowired
    Tenantervice tenantervice;

    @Autowired
    SysUserService sysUserService;

    /**
     * 租户列表
     *
     * @return
     */
    @Permission(code = "tenant_list")
    @PostMapping("/getTenantByPage")
    public Pager<TenantInfoDto> userList(@RequestBody TenantInfoParam tenantInfoParam) {
        return tenantervice.getTenantByPage(tenantInfoParam);
    }

    /**
     * 创建租户
     *
     * @return
     */
    @Permission(code = "tenant_add")
    @PostMapping("/create")
    public ResponseData createTenant(@Validated(BaseEntity.Create.class) @RequestBody TenantInfoParam tenantInfoParam) {
        int count = new QTenantInfo().name.eq(tenantInfoParam.getName()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.TENANT_NAME_EXISTS);
        }

        count = new QTenantInfo().account.eq(tenantInfoParam.getAccount()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.TENANT_ACCOUNT_EXISTS);
        }

        return ResponseData.success(tenantervice.createTenant(tenantInfoParam));
    }


    /**
     * 更新租户
     *
     * @return
     */
    @Permission(code = "tenant_update")
    @PostMapping("/update")
    public ResponseData updateTenant(@Validated(value = BaseEntity.Update.class) @RequestBody TenantInfoParam tenantInfoParam) {
        int count = new QTenantInfo().name.eq(tenantInfoParam.getName()).tenantId.ne(tenantInfoParam.getTenantId()).findCount();
        if (count > 0) {
            throw new ServiceException(BizExceptionEnum.TENANT_NAME_EXISTS);
        }
        return ResponseData.success(tenantervice.updateTenant(tenantInfoParam));
    }

    /**
     * 删除租户
     *
     * @return
     */
    @Permission(code = "tenant_delete")
    @PostMapping("/delete")
    public ResponseData deleteTenant(@Validated(BaseEntity.Delete.class) @RequestBody TenantInfoParam tenantInfoParam) {
        tenantervice.deleteTenant(tenantInfoParam);
        return ResponseData.success();
    }

    /**
     * 租户状态修改
     *
     * @return
     */
    @Permission(code = "tenant_update")
    @RequestMapping("/status")
    public ResponseData status(@RequestParam("tenantId") Long tenantId, @RequestParam("status") String status) {
        tenantervice.status(tenantId,status);
        return ResponseData.success();
    }

    /**
     * 租户详情
     *
     * @return
     */
    @Permission(code = "tenant_update")
    @RequestMapping("/detail")
    public ResponseData detail(@RequestParam Long tenantId) {
        TenantInfoDto tenantInfoDto = new QTenantInfo().tenantId.eq(tenantId).asDto(TenantInfoDto.class).findOne();
        return ResponseData.success(tenantInfoDto);
    }

    /**
     * 重置密码
     *
     * @return
     */
    @RequestMapping("/reset")
    @Permission(code = "reset")
    public ResponseData reset(@RequestParam("account") String account) {
        SysUser sysUser = new QSysUser().account.eq(account).findOne();
        if (sysUser == null) {
            throw new ServiceException(BizExceptionEnum.TENANT_NOT_EXISTS);
        }
        sysUserService.reset(sysUser.getUserId());
        return ResponseData.success();
    }
}
