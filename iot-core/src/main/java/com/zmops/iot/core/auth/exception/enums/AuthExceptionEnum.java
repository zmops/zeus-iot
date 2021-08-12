package com.zmops.iot.core.auth.exception.enums;

import com.zmops.iot.model.exception.AbstractBaseExceptionEnum;
import lombok.Getter;

/**
 * 认证失败的异常枚举
 *
 * @author fengshuonan
 */
@Getter
public enum AuthExceptionEnum implements AbstractBaseExceptionEnum {

    NOT_LOGIN_ERROR(1401, "用户未登录"),

    NOT_EXIST_ERROR(1408, "用户不存在"),

    USERNAME_PWD_ERROR(1402, "账号密码错误"),

    LOGIN_EXPPIRED(1403, "登录已过期，请重新登录"),

    ACCOUNT_FREEZE_ERROR(1404, "账号被冻结"),

    NO_ROLE_ERROR(1405, "用户没有分配角色，获取菜单失败"),

    VALID_CODE_ERROR(1406, "验证码错误"),

    ZBX_LOGIN_ERROR(1407, "Zabbix平台登录获取Token失败"),

    //用在PermissonException
    NO_PERMISSION(1500, "用户没有此操作权限"),

    NO_PAGE_ERROR(1502, "请求接口不存在或用户未登录"),

    LOGIN_TIMEOUT(409, "登录超时，请重新登录！");

    AuthExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private Integer code;

    private String message;

}
