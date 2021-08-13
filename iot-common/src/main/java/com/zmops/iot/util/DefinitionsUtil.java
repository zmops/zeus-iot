package com.zmops.iot.util;

import com.google.common.collect.Table;
import com.zmops.iot.domain.sys.SysUser;
import com.zmops.iot.model.cache.DictionaryCache;
import com.zmops.iot.model.cache.SysRoleCache;
import com.zmops.iot.model.cache.SysUserCache;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 枚举类工具类
 *
 * @author yefei
 */
public class DefinitionsUtil {

    @Getter
    private static DictionaryCache dictionaryCache = new DictionaryCache();


    @Getter
    @Setter
    private static SysUserCache sysUserCache = new SysUserCache();

    @Getter
    @Setter
    private static SysRoleCache sysRoleCache = new SysRoleCache();

    public static void updateDictionaries(Table<String, String, String> dictionarys) {
        dictionaryCache.updateDictionaries(dictionarys);
    }

    public static void updateSysUser(List<SysUser> memberList) {
        sysUserCache.updateSysUser(memberList);
    }

    public static String getNameByVal(String flag, String value) {
        return dictionaryCache.getNameByVal(flag, value);
    }


    public static String getSysUserName(Long sysUserId) {
        return sysUserCache.getSysUserName(sysUserId);
    }

    public static SysUser getSysUser(Long sysUserId) {
        return sysUserCache.getSysUser(sysUserId);
    }

}
