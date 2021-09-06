package com.zmops.zeus.iot.server.core.analysis;

/**
 * @author nantian created at 2021/9/6 16:49
 */
public interface StreamProcessor<STREAM> {

    /**
     * 写入数据
     *
     * @param stream
     */
    void in(STREAM stream);
}
