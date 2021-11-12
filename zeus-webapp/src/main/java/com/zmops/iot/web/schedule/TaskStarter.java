package com.zmops.iot.web.schedule;

import com.zmops.iot.schedule.config.ScheduleConfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author nantian created at 2021/11/12 22:35
 */

@Component
public class TaskStarter implements InitializingBean, DisposableBean {

    private TaskTriggerPool triggerPool;
    private TaskScheduleImpl schedule;

    @Override
    public void destroy() throws Exception {
        schedule.toStop();
        triggerPool.stop();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        ScheduleConfig scheduleConfig = new ScheduleConfig();

        triggerPool = new TaskTriggerPool(scheduleConfig);
        triggerPool.start();

        schedule = new TaskScheduleImpl(scheduleConfig);
        schedule.start();
    }
}
