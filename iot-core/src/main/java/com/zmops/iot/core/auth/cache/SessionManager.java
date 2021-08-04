package com.zmops.iot.core.auth.cache;


import com.zmops.iot.core.auth.model.LoginUser;

/**
 * 会话管理
 *
 * @author fengshuonan
 */
public interface SessionManager {

    /**
     * 缓存前缀
     */
    String SESSION_PREFIX = "LOGIN_USER_";

    /**
     * 创建会话
     *
     * @author fengshuonan
     */
    void createSession(String token, LoginUser loginUser);

    /**
     * 获取会话
     *
     * @author fengshuonan
     */
    LoginUser getSession(String token);

    /**
     * 删除会话
     *
     * @author fengshuonan
     */
    void removeSession(String token);

    /**
     * 是否已经登陆
     *
     * @author fengshuonan
     */
    boolean haveSession(String token);

}
