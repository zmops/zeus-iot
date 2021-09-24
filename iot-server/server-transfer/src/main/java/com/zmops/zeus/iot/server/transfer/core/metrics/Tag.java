package com.zmops.zeus.iot.server.transfer.core.metrics;

public class Tag implements MetricSnapshot<String> {

    private String name;

    /**
     * set string name for tag.
     *
     * @param name - tag name
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String snapshot() {
        return name;
    }
}
