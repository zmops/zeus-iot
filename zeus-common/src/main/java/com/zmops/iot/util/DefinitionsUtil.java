package com.zmops.iot.util;

import com.google.common.collect.Table;
import com.zmops.iot.domain.product.ProductServiceParam;
import com.zmops.iot.domain.sys.SysUser;
import com.zmops.iot.model.cache.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

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

    @Getter
    @Setter
    private static ProductTypeCache productTypeCache = new ProductTypeCache();

    @Getter
    @Setter
    private static ProductServiceCache productServiceCache = new ProductServiceCache();

    @Getter
    @Setter
    private static ProductServiceParamCache productServiceParamCache = new ProductServiceParamCache();

    @Getter
    @Setter
    private static DeviceCache deviceCache = new DeviceCache();

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

    public static void updateProductType(Map<Long, String> map) {
        productTypeCache.updateProductType(map);
    }

    public static String getTypeName(long value) {
        return productTypeCache.getTypeName(value);
    }

    public static void updateServiceCache(Map<Long, String> map) {
        productServiceCache.updateProductService(map);
    }

    public static String getServiceName(long value) {
        return productServiceCache.getServiceName(value);
    }

    public static void updateServiceParamCache(Map<Long, List<ProductServiceParam>> paramMap) {
        productServiceParamCache.updateProductServiceParam(paramMap);
    }

    public static List<ProductServiceParam> getServiceParam(long value) {
        return productServiceParamCache.getServiceParam(value);
    }

    public static void updateDeviceCache(Map<String, String> map) {
        deviceCache.updateDeviceName(map);
    }

    public static String getDeviceName(String value) {
        return deviceCache.getDeviceName(value);
    }
}
