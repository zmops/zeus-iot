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
package com.zmops.iot.web.auth;


import com.zmops.iot.core.auth.exception.PermissionException;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

/**
 * 权限检查的aop
 *
 * @author fengshuonan
 */
@Aspect
@Order(200)
public class PermissionAop {

    @Autowired
    private AuthService authService;

    @Pointcut(value = "@annotation(com.zmops.iot.web.auth.Permission)")
    private void cutPermission() {

    }

    @Around("cutPermission()")
    public Object doPermission(ProceedingJoinPoint point) throws Throwable {
        MethodSignature ms = (MethodSignature) point.getSignature();
        Method method = ms.getMethod();
        Permission permission = method.getAnnotation(Permission.class);
        String[] permissions = permission.value();
        String code = permission.code();
        if (permissions.length == 0 && StringUtils.isNotBlank(code)) {
            //检查全体角色
            boolean result = authService.checkAll(code);
            if (result) {
                return point.proceed();
            } else {
                throw new PermissionException();
            }
        } else {
            //检查指定角色
            boolean result = authService.check(permissions);
            if (result) {
                return point.proceed();
            } else {
                throw new PermissionException();
            }
        }
    }

}
