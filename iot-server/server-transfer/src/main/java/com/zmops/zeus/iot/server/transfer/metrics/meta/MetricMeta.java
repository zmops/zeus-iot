package com.zmops.zeus.iot.server.transfer.metrics.meta;

import com.zmops.zeus.iot.server.transfer.metrics.Metric;
import com.zmops.zeus.iot.server.transfer.metrics.counter.CounterInt;
import com.zmops.zeus.iot.server.transfer.metrics.counter.CounterLong;
import com.zmops.zeus.iot.server.transfer.metrics.gauge.GaugeInt;
import com.zmops.zeus.iot.server.transfer.metrics.gauge.GaugeLong;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

import static com.zmops.zeus.iot.server.transfer.metrics.Metric.Type.*;

/**
 * this class is related to {@link Metric}
 */
public class MetricMeta {

    private String name;
    private String type;
    private String desc;
    private Field field;

    public static MetricMeta build(Metric annotation, Field field) {
        MetricMeta metricMeta = new MetricMeta();
        metricMeta.name = StringUtils.capitalize(field.getName());
        metricMeta.desc = annotation.desc();
        metricMeta.type = DEFAULT.getValue();
        metricMeta.field = field;
        Class<?> clz = field.getType();
        if (clz.isAssignableFrom(CounterLong.class)) {
            metricMeta.type = COUNTER_LONG.getValue();
        } else if (clz.isAssignableFrom(CounterInt.class)) {
            metricMeta.type = COUNTER_INT.getValue();
        } else if (clz.isAssignableFrom(GaugeInt.class)) {
            metricMeta.type = GAUGE_INT.getValue();
        } else if (clz.isAssignableFrom(GaugeLong.class)) {
            metricMeta.type = GAUGE_LONG.getValue();
        }
        return metricMeta;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public Field getField() {
        return field;
    }
}

