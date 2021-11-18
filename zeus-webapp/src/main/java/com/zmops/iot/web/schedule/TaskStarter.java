package com.zmops.iot.web.schedule;


import com.zmops.iot.web.device.service.SceneScheduleProcessor;
import com.zmops.iot.web.schedule.config.ScheduleConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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

    @Autowired
    private ApplicationEventPublisher publisher;

    @Override
    public void destroy() throws Exception {
        schedule.toStop();
        triggerPool.stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        ScheduleConfig scheduleConfig = new ScheduleConfig();

        triggerPool = new TaskTriggerPool(scheduleConfig, publisher);
        triggerPool.start();

        schedule = new TaskScheduleImpl(scheduleConfig);
        schedule.start();
    }
}
