package com.zmops.iot.core.auth.exception;

import com.zmops.iot.model.exception.AbstractBaseExceptionEnum;
import lombok.Data;

import static com.zmops.iot.core.auth.exception.enums.AuthExceptionEnum.NO_PERMISSION;

/**
 * 没有访问权限
 *
 * @author fengshuonan
 */
@Data
public class PermissionException extends RuntimeException {

    private Integer code;
    private String errorMessage;

    public PermissionException() {
        super(NO_PERMISSION.getMessage());
        this.code = NO_PERMISSION.getCode();
        this.errorMessage = NO_PERMISSION.getMessage();
    }

    public PermissionException(AbstractBaseExceptionEnum exception) {
        super(exception.getMessage());
        this.code = exception.getCode();
        this.errorMessage = exception.getMessage();
    }

}
