package com.zmops.iot.async.exception;

/**
 * 整组取消，设置该异常。
 *
 * @author tcsnzh[zh.jobs@foxmail.com] create this in 2021/5/25-下午6:12
 */
public class CancelException extends EndsNormallyException {
    public CancelException() {
    }

    public CancelException(String message) {
        super(message);
    }
}
