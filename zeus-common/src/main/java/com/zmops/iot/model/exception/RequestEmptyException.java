package com.zmops.iot.model.exception;

/**
 * @author nantian created at 2021/7/29 20:35
 */
public class RequestEmptyException extends ServiceException {

    public RequestEmptyException() {
        super(400, "请求数据不完整或格式错误！");
    }

    public RequestEmptyException(String errorMessage) {
        super(400, errorMessage);
    }

    /**
     * 不拷贝栈信息，提高性能
     *
     * @author fengshuonan
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
