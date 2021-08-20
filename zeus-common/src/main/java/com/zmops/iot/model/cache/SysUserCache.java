package com.zmops.iot.model.cache;


import com.zmops.iot.domain.sys.SysUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author yefei
 **/
public class SysUserCache extends AbstractCache<Long, SysUser> {

    public SysUserCache() {
        super();
    }

    public void updateSysUser(List<SysUser> memberList) {
        List<SysUser> users = new ArrayList<>(memberList);
        update(users, SysUser::getUserId);
    }

    public String getSysUserName(Long sysUserId) {
        return Optional.ofNullable(get(sysUserId)).map(SysUser::getName).orElse(null);
    }

    public SysUser getSysUser(Long sysUserId) {
        return get(sysUserId);
    }

}
