package com.zmops.iot.core.util;


import com.zmops.iot.util.ToolUtil;

/**
 * 密码加盐的工具
 *
 * @author yefei
 */
public class SaltUtil {

    /**
     * 获取密码盐
     *
     * @author yefei
     */
    public static String getRandomSalt() {
        return ToolUtil.getRandomString(5);
    }

    /**
     * md5加密，带盐值
     *
     * @author yefei
     */
    public static String md5Encrypt(String password, String salt) {
        if (ToolUtil.isOneEmpty(password, salt)) {
            throw new IllegalArgumentException("密码为空！");
        } else {
            return MD5Util.encrypt(password + salt);
        }
    }

}
