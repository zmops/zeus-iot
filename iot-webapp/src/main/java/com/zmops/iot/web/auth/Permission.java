package com.zmops.iot.web.auth;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Permission {

    /**
     * <p>角色英文名称</p>
     * <p>使用注解时加上这个值表示限制只有某个角色的才可以访问对应的资源</p>
     * <p>常用在某些资源限制只有超级管理员角色才可访问</p>
     */
    String[] value() default {};


    /**
     * 操作code
     *
     * @return
     */
    String code();
}
