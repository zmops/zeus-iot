package com.zmops.iot.async.exception;

/**
 * 该异常表示此任务单元是正常结束的。
 *
 * @author tcsnzh[zh.jobs@foxmail.com] create this in 2021/6/5-下午11:57
 */
public abstract class EndsNormallyException extends RuntimeException {
    public EndsNormallyException() {
    }

    public EndsNormallyException(String message) {
        super(message);
    }

    public EndsNormallyException(String message, Throwable cause) {
        super(message, cause);
    }

    public EndsNormallyException(Throwable cause) {
        super(cause);
    }

    public EndsNormallyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
