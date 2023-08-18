package com.zmops.zeus.iot.server.core.worker.data;

/**
 * @author nantian created at 2021/8/23 15:14
 * <p>
 * 对应设备的属性
 */
public interface Item {

    /**
     * 设备ID 唯一
     *
     * @return string
     */
    String host();

    /**
     * 属性标识 唯一
     *
     * @return string
     */
    String key();
}
