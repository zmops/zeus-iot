package com.zmops.iot.model.cache.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字典翻译注解 @Repeatable
 * @author yefei
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CachedValue {

    /**
     * 字段翻译的类型
     *
     * @return
     */
    DicType type() default DicType.Dictionary;


    /**
     * 翻译后的字段名
     *
     * @return
     */
    String fieldName();


    /**
     * 当type()为Dictionary时 翻译的字典类型
     *
     * @return
     */
    String value() default "";

}
