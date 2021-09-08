package com.zmops.iot.web.device.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author yefei
 **/
@EnableScheduling
@Component
@Slf4j
public class DeviceOnlineReprotSchedule {

    @Scheduled(cron = "0 59 23 1/1 * ? ")
    public void report(){
        log.info("开始查询设备在线情况");

        //TODO 统计出 当前 设备在线情况

        //插入 在线情况表

    }

}
