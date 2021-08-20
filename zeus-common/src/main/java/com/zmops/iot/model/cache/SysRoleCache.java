package com.zmops.iot.model.cache;


import com.zmops.iot.domain.sys.SysRole;

import java.util.List;
import java.util.Optional;

public class SysRoleCache extends AbstractCache<Long, SysRole> {

    public SysRoleCache() {
        super();
    }

    public void updateSysRoleCache(List<SysRole> restRoleList) {
        update(restRoleList, SysRole::getRoleId);
    }

    public String getSysRoleName(Long id) {
        return Optional.ofNullable(get(id)).map(SysRole::getName).orElse(null);
    }

    public SysRole getRestRole(Long id) {
        return get(id);
    }
}
