package com.zmops.iot.model.exception;

/**
 * @author nantian created at 2021/8/4 9:52
 * <p>
 * Zabbix 接口调用异常
 */
public class ZbxApiException extends ServiceException {


    public ZbxApiException(Integer code, String errorMessage) {
        super(code, errorMessage);
    }

    public ZbxApiException(AbstractBaseExceptionEnum exception) {
        super(exception);
    }
}
