package com.zmops.iot.core.auth.context;

import com.zmops.iot.core.auth.model.LoginUser;

import java.util.List;

/**
 * 当前登录用户信息获取的接口
 *
 * @author fengshuonan
 */
public interface LoginContext {

    /**
     * 获取当前登录用户
     *
     * @author fengshuonan
     */
    LoginUser getUser();

    /**
     * 获取当前登录用户的token
     *
     * @author fengshuonan
     */
    String getToken();


    /**
     * 获取 Zabbix 登陆 Token
     *
     * @return
     */
    String getZbxToken();

    /**
     * 是否登录
     *
     * @author fengshuonan
     */
    boolean hasLogin();

    /**
     * 获取当前登录用户id
     *
     * @author fengshuonan
     */
    Long getUserId();

    /**
     * 验证当前用户是否包含该角色
     *
     * @param roleName 角色名称
     * @return 包含:true, 否则false
     */
    boolean hasRole(String roleName);

    /**
     * 验证当前用户是否属于以下任意一个角色
     *
     * @param roleNames 角色列表,逗号分隔
     * @return 包含:true, 否则false
     */
    boolean hasAnyRoles(String roleNames);

    /**
     * 验证当前用户是否拥有指定权限
     *
     * @param permission 权限名
     * @return 拥有权限：true，否则false
     */
    boolean hasPermission(String permission);

    /**
     * 判断当前用户是否是超级管理员
     */
    boolean isAdmin();

    /**
     * 判断用户是否是从oauth2登录过来的
     */
    boolean oauth2Flag();

}
