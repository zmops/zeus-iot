package com.zmops.zeus.iot.server.transfer.core.metrics.meta;

import com.zmops.zeus.iot.server.transfer.core.metrics.Metrics;

import java.util.List;

/**
 * This class is related to {@link Metrics}
 */
public class MetricsMeta {

    private String           context;
    private String           desc;
    private String           name;
    private List<MetricMeta> metricMetaList;

    private MetricsMeta() {
    }

    public static MetricsMeta build(Metrics metrics, List<MetricMeta> metricMetaList) {
        MetricsMeta metricsMeta = new MetricsMeta();
        metricsMeta.context = metrics.context();
        metricsMeta.desc = metrics.desc();
        metricsMeta.name = metrics.name();
        metricsMeta.metricMetaList = metricMetaList;
        return metricsMeta;
    }

    public String getContext() {
        return context;
    }

    public String getDesc() {
        return desc;
    }

    public List<MetricMeta> getMetricMetaList() {
        return metricMetaList;
    }

    public String getName() {
        return name;
    }
}
