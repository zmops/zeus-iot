package com.zmops.iot.model.exception.enums;

import com.zmops.iot.model.exception.AbstractBaseExceptionEnum;

/**
 * @author nantian created at 2021/7/29 17:12
 */
public enum CoreExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 其他
     */
    INVLIDE_DATE_STRING(400, "输入日期格式不对"),

    /**
     * 初始化数据库的异常
     */
    NO_CURRENT_USER(700, "当前没有登录的用户"),
    INIT_TABLE_EMPTY_PARAMS(701, "初始化数据库，存在为空的字段"),

    /**
     * 其他
     */
    WRITE_ERROR(500, "渲染界面错误"),
    ENCRYPT_ERROR(600, "加解密错误"),

    /**
     * 文件上传
     */
    FILE_READING_ERROR(400, "FILE_READING_ERROR!"),
    FILE_NOT_FOUND(400, "FILE_NOT_FOUND!"),

    /**
     * 数据库字段与实体字段不一致
     */
    FIELD_VALIDATE_ERROR(700, "数据库字段与实体字段不一致!"),

    /**
     * 错误的请求
     */
    PAGE_NULL(404, "请求页面不存在"),
    IO_ERROR(500, "流读取异常"),
    SERVICE_ERROR(500, "服务器异常"),
    REMOTE_SERVICE_NULL(404, "远程服务不存在"),
    ASYNC_ERROR(5000, "数据在被别人修改，请稍后重试"),
    REPEATED_SUBMIT(500,"请勿重复请求");

    CoreExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private Integer code;

    private String message;

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
