package com.zmops.iot;

import com.dtflys.forest.springboot.annotation.ForestScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

/**
 * @author nantian
 * <p>
 * ### 宙斯物联网中台 ZEUS-IOT == SpringBoot + Ebean 极简架构
 */
@SpringBootApplication
@ForestScan("com.zmops.zeus.driver.service")
public class IoTApplication {

    private final static Logger logger = LoggerFactory.getLogger(IoTApplication.class);

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(IoTApplication.class, args);
        logger.info(IoTApplication.class.getSimpleName() + " is success!");
    }
}
