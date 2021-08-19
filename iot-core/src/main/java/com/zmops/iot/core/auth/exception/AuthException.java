package com.zmops.iot.core.auth.exception;


import com.zmops.iot.model.exception.AbstractBaseExceptionEnum;
import lombok.Data;

/**
 * 认证失败（账号密码错误，账号被冻结，token过期等）
 *
 * @author fengshuonan
 */
@Data
public class AuthException extends RuntimeException {

    private Integer code;
    private String  errorMessage;

    public AuthException() {
        super("认证失败！");
        this.code = 500;
        this.errorMessage = "认证失败！";
    }

    public AuthException(AbstractBaseExceptionEnum exception) {
        super(exception.getMessage());
        this.code = exception.getCode();
        this.errorMessage = exception.getMessage();
    }

}
