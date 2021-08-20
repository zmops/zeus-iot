package com.zmops.iot.model.exception;

/**
 * @author nantian created at 2021/7/29 17:11
 */
public interface AbstractBaseExceptionEnum {

    /**
     * 获取异常的状态码
     */
    Integer getCode();

    /**
     * 获取异常的提示信息
     */
    String getMessage();
}
