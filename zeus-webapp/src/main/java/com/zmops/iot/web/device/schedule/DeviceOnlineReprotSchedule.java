package com.zmops.iot.web.device.schedule;

import com.zmops.iot.domain.device.Device;
import com.zmops.iot.domain.device.DeviceOnlineReport;
import com.zmops.iot.domain.device.query.QDevice;
import com.zmops.iot.util.LocalDateTimeUtils;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author yefei
 **/
@EnableScheduling
@Component
@Slf4j
public class DeviceOnlineReprotSchedule {

    @Scheduled(cron = "0 59 23 1/1 * ? ")
    public void report() {
        log.info("开始查询设备在线情况");

        //统计出 当前 设备在线情况
        List<Device> deviceList = new QDevice().findList();

        Map<String, Map<Integer, Long>> onLinemap = deviceList.parallelStream()
                .collect(Collectors.groupingBy(Device::getType, Collectors.groupingBy(Device::getOnline, Collectors.counting())));

        List<DeviceOnlineReport> list = new ArrayList<>();

        onLinemap.forEach((key, value) -> {
            list.add(DeviceOnlineReport.builder().type(Integer.parseInt(key))
                    .createTime(LocalDateTimeUtils.formatTimeDate(LocalDateTime.now()))
                    .online(Optional.ofNullable(value.get(1)).orElse(0L))
                    .offline(Optional.ofNullable(value.get(0)).orElse(0L)).build());
        });
        //插入 在线情况表
        DB.saveAll(list);
    }

}
