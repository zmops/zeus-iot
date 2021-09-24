package com.zmops.zeus.iot.server.transfer.core.metrics;

import com.zmops.zeus.iot.server.transfer.core.metrics.meta.MetricMeta;
import com.zmops.zeus.iot.server.transfer.core.metrics.meta.MetricsMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dynamic MBean for agent
 */
public class AgentDynamicMBean implements DynamicMBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentDynamicMBean.class);

    private final ConcurrentHashMap<String, MetricSnapshot<?>> snapshotAttrs = new ConcurrentHashMap<>();

    private final MBeanInfo                mBeanInfo;
    private final List<MBeanAttributeInfo> attrs;
    private final MetricsMeta              metricsMeta;
    private final String                   module;
    private final String                   aspect;
    private final String                   desc;

    public AgentDynamicMBean(String module, String aspect, String desc, MetricsMeta metricsMeta, Object source) {
        this.module = module;
        this.aspect = aspect;
        this.desc = desc;
        this.metricsMeta = metricsMeta;
        this.attrs = new ArrayList<>();
        this.mBeanInfo = metricsMetaToInfo();

        formatSnapshotList(source);
    }

    private void formatSnapshotList(Object source) {
        for (MetricMeta metricMeta : this.metricsMeta.getMetricMetaList()) {
            try {
                snapshotAttrs.put(metricMeta.getName(), (MetricSnapshot<?>) metricMeta.getField().get(source));
            } catch (Exception ex) {
                LOGGER.error("exception while adding snapshot list", ex);
            }
        }
    }


    private MBeanInfo metricsMetaToInfo() {
        // overwrite name, desc from MetricsMeta if not null.
        String name        = this.module == null ? metricsMeta.getName() : this.module;
        String description = this.desc == null ? metricsMeta.getDesc() : this.desc;

        for (MetricMeta fieldMetricMeta : metricsMeta.getMetricMetaList()) {
            attrs.add(new MBeanAttributeInfo(fieldMetricMeta.getName(),
                    fieldMetricMeta.getType(), fieldMetricMeta.getDesc(), true, false, false));
        }
        return new MBeanInfo(name, description, attrs.toArray(new MBeanAttributeInfo[0]),
                null, null, null);
    }

    @Override
    public Object getAttribute(String attribute) {
        MetricSnapshot<?> snapshot = snapshotAttrs.get(attribute);
        return new Attribute(attribute, snapshot.snapshot());
    }

    @Override
    public void setAttribute(Attribute attribute) {
        throw new UnsupportedOperationException("Metrics are read-only.");
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        AttributeList attributeList = new AttributeList();
        for (String attrKey : attributes) {
            MetricSnapshot<?> snapshot = snapshotAttrs.get(attrKey);
            if (snapshot != null) {
                attributeList.add(new Attribute(attrKey, snapshot.snapshot()));
            }
        }
        return attributeList;
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        throw new UnsupportedOperationException("Metrics are read-only.");
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return mBeanInfo;
    }

    public String getModule() {
        return module;
    }

    public String getAspect() {
        return aspect;
    }
}
