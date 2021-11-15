package com.zmops.iot.web.schedule;


import com.zmops.iot.web.device.service.SceneScheduleProcessor;
import com.zmops.iot.web.schedule.config.ScheduleConfig;
import com.zmops.zeus.iot.server.eventbus.core.EventControllerFactory;
import com.zmops.zeus.iot.server.eventbus.thread.ThreadPoolFactory;
import com.zmops.zeus.iot.server.eventbus.thread.entity.ThreadCustomization;
import com.zmops.zeus.iot.server.eventbus.thread.entity.ThreadParameter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author nantian created at 2021/11/12 22:35
 */

@Component
public class TaskStarter implements InitializingBean, DisposableBean {

    private TaskTriggerPool  triggerPool;
    private TaskScheduleImpl schedule;

    @Autowired
    SceneScheduleProcessor sceneScheduleProcessor;

    @Override
    public void destroy() throws Exception {
        schedule.toStop();
        triggerPool.stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        ScheduleConfig scheduleConfig = new ScheduleConfig();

        ThreadPoolFactory threadPoolFactory = new ThreadPoolFactory(new ThreadCustomization(), new ThreadParameter());

        EventControllerFactory eventControllerFactory = new EventControllerFactory(threadPoolFactory);

        triggerPool = new TaskTriggerPool(scheduleConfig,eventControllerFactory);
        triggerPool.start();

        eventControllerFactory.getAsyncController("scene").register(sceneScheduleProcessor);

        schedule = new TaskScheduleImpl(scheduleConfig);
        schedule.start();
    }
}
