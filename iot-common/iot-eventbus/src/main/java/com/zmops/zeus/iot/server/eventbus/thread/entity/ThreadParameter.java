package com.zmops.zeus.iot.server.eventbus.thread.entity;


import com.zmops.zeus.iot.server.eventbus.thread.constant.ThreadConstant;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

@Getter
@Setter
public class ThreadParameter implements Serializable {
    private static final long serialVersionUID = 6869706244434951605L;

    private int threadPoolCorePoolSize = ThreadConstant.CPUS;
    private int threadPoolMaximumPoolSize = ThreadConstant.CPUS * 2;
    private long threadPoolKeepAliveTime = 900000;
    private boolean threadPoolAllowCoreThreadTimeout = false;
    private String threadPoolQueue = "LinkedBlockingQueue";
    private int threadPoolQueueCapacity = ThreadConstant.CPUS * 128;
    private String threadPoolRejectedPolicy = "BlockingPolicyWithReport";


    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(this, object);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}