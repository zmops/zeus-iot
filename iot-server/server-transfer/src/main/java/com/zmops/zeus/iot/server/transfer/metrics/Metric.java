package com.zmops.zeus.iot.server.transfer.metrics;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * metric for field
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Metric {

    /**
     * Type of metric
     *
     * @return metric type
     */
    Type type() default Type.DEFAULT;

    /**
     * Doc of metric
     *
     * @return metric doc
     */
    String desc() default "";

    enum Type {
        DEFAULT("java.lang.String"),
        COUNTER_INT("java.lang.Integer"),
        COUNTER_LONG("java.lang.Long"),
        GAUGE_INT("java.lang.Integer"),
        GAUGE_LONG("java.lang.Long"),
        TAG("java.lang.String");
        private final String value;

        private Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
