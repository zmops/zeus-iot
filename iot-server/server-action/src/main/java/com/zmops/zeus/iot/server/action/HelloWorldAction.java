package com.zmops.zeus.iot.server.action;

import com.google.common.eventbus.Subscribe;
import com.zmops.zeus.iot.server.action.bean.ActionParam;
import lombok.extern.slf4j.Slf4j;

/**
 * @author nantian created at 2021/8/24 0:07
 * <p>
 * 动作触发示例，基于 Camel 动态执行
 */

@Slf4j
public class HelloWorldAction {


    @Subscribe
    public void subscribe(ActionParam event) {
        log.info("子线程接收异步事件, 设备ID - {} ", event.getTriggerDevice());
    }

}
