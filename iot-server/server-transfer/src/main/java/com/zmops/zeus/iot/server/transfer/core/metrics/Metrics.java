package com.zmops.zeus.iot.server.transfer.core.metrics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * metric
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Metrics {

    /**
     * Metrics name
     *
     * @return
     */
    String name() default "";

    /**
     * Metrics context
     *
     * @return
     */
    String context() default "";

    /**
     * Metrics description
     *
     * @return
     */
    String desc() default "";
}
