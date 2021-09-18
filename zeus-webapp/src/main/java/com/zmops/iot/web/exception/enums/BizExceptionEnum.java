/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zmops.iot.web.exception.enums;

import com.zmops.iot.model.exception.AbstractBaseExceptionEnum;

/**
 * @author nantian
 * @Description 所有业务异常的枚举
 */
public enum BizExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 字典
     */
    DICT_EXISTED(400, "字典已经存在"),
    ERROR_CREATE_DICT(500, "创建字典失败"),
    ERROR_WRAPPER_FIELD(500, "包装字典属性失败"),
    ERROR_CODE_EMPTY(500, "字典类型不存在"),
    DICT_NOT_EXIST(400, "字典项不存在"),
    SYSTEM_DICT_CANNOT_DELETE(400, "系统字典不能删除"),
    /**
     * 文件上传
     */
    FILE_READING_ERROR(400, "FILE_READING_ERROR!"),
    FILE_NOT_FOUND(400, "FILE_NOT_FOUND!"),
    UPLOAD_ERROR(500, "上传图片出错"),

    /**
     * 权限和数据问题
     */
    USER_NOT_BIND_ROLE(400, "用户没有绑定角色"),
    DB_RESOURCE_NULL(400, "数据库中没有该资源"),
    NO_PERMITION(405, "权限异常"),
    REQUEST_INVALIDATE(400, "请求数据格式不正确"),
    INVALID_KAPTCHA(400, "验证码不正确"),
    CANT_DELETE_ADMIN(600, "不能删除超级管理员"),
    CANT_FREEZE_ADMIN(600, "不能冻结超级管理员"),
    CANT_CHANGE_ADMIN(600, "不能修改超级管理员角色"),
    ROLE_HAS_EXIST(601, "角色已存在"),
    ROLE_HAS_BIND_USER(602, "角色绑定了用户,请先解除绑定!"),
    MENU_NOT_EXIST_OR_NO_PRERMISSION(603, "菜单不存在或无权授权此菜单"),
    ROLE_NOT_EXIST(604, "角色不存在"),

    /**
     * 账户问题
     */
    NOT_LOGIN(401, "当前用户未登录"),
    USER_ALREADY_REG(401, "该用户已经注册"),
    NO_THIS_USER(400, "没有此用户"),
    USER_NOT_EXISTED(400, "没有此用户"),
    ACCOUNT_FREEZED(401, "账号被冻结"),
    OLD_PWD_NOT_RIGHT(402, "原密码不正确"),
    TWO_PWD_NOT_MATCH(405, "两次输入密码不一致"),
    PWD_DECRYPT_ERR(406, "密码解密出错"),

    /**
     * 错误的请求
     */
    MENU_PCODE_COINCIDENCE(400, "菜单编号和副编号不能一致"),
    EXISTED_THE_MENU(400, "菜单编号重复，不能添加"),
    DICT_MUST_BE_NUMBER(400, "字典的值必须为数字"),
    REQUEST_NULL(400, "请求有错误"),
    SESSION_TIMEOUT(400, "会话超时"),
    SERVER_ERROR(500, "服务器异常"),

    /**
     * token异常
     */
    TOKEN_EXPIRED(700, "token过期"),
    TOKEN_ERROR(700, "token验证失败"),

    /**
     * 签名异常
     */
    SIGN_ERROR(700, "签名验证失败"),

    /**
     * 系统常量
     */
    ALREADY_CONSTANTS_ERROR(400, "已经存在该编码的系统参数"),
    SYSTEM_CONSTANT_ERROR(400, "不能删除系统常量"),


    /**
     * zabbix 相关
     */

    ZBX_TOKEN_SAVE_ERROR(800, "Zabbix登陆token保存失败"),
    ZBX_CALL_ERR(801, "ZABBIX接口请求失败"),

    /**
     * 用户组相关
     */
    USERGROUP_HAS_EXIST(1001, "用户组已存在"),
    USERGROUP_NOT_EXIST(1003, "用户组不存在"),
    USERGROUP_HAS_BIND_USER(1002, "用户组绑定了用户,请先解除绑定!"),

    /**
     * 用户相关
     */
    USER_NOT_EXIST(1101, "用户不存在"),


    /**
     * 设备组相关
     */
    DEVICEGROUP_HAS_EXIST(1201, "设备组已存在"),
    DEVICEGROUP_NOT_EXIST(1203, "设备组不存在"),
    DEVICEGROUP_HAS_BIND_USERGRP(1202, "设备组绑定了用户组,请先解除绑定!"),
    DEVICEGROUP_HAS_BIND_DEVICE(1203, "设备组绑定了设备,请先解除绑定!"),

    /**
     * 产品相关
     */
    PRODUCT_HAS_EXIST(1301, "该产品ID已存在"),
    PRODUCT_NOT_EXISTS(1302, "该产品不存在"),
    PRODUCT_TYPE_EXIST(1303, "产品分类已存在"),
    PRODUCT_TYPE_NOT_EXIST(1303, "产品分类不存在"),
    PRODUCT_TYPE_HAS_BIND(1304, "产品分类已绑定产品"),
    PRODUCT_TYPE_PID_NOT_EXIST(1305, "父节点不存在"),
    PRODUCT_TYPE_PID_ERR(1306, "父节点不可以是自已或子节点"),
    PRODUCT_HAS_BIND_DEVICE(1307, "产品已绑定设备"),

    /**
     * 属性相关
     */
    PRODUCT_ATTR_KEY_EXISTS(1401, "该属性Key已存在"),
    PRODUCT_ATTR_KEY_NOT_EXISTS(1404, "该属性Key不存在"),
    PRODUCT_ATTR_DEPTED_NULL(1402,"依赖的属性不能为空"),
    PRODUCT_ATTR_DEPTED_NOT_EXIST(1403,"依赖的属性不能为空"),

    /**
     * 设备相关
     */
    DEVICE_EXISTS(1501, "设备名称已存在"),
    DEVICE_NOT_EXISTS(1502, "设备不存在"),

    /**
     * 服务相关
     */
    SERVICE_EXISTS(1601, "服务已存在"),
    SERVICE_NOT_EXISTS(1602, "服务不存在"),

    /**
     * 触发规则相关
     */
    RULE_NOT_EXISTS(1701, "上下线规则不存在"),
    RULE_EXISTS(1702, "上下线规则已存在"),

    EVENT_HAS_NOT_DEVICE(1703,"告警规则缺少关联产品或设备"),
    EVENT_NOT_EXISTS(1704, "告警规则不存在"),

    /**
     * 租户相关的异常
     */
    AUTH_REQUEST_ERROR(400, "账号密码错误");


    BizExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private Integer code;

    private String message;

    @Override
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
