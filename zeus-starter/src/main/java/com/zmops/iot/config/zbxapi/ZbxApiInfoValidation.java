package com.zmops.iot.config.zbxapi;

import cn.hutool.core.util.NumberUtil;
import com.zmops.zeus.driver.service.ZbxApiInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author nantian created at 2021/8/3 12:03
 */
@Slf4j
@Component
public class ZbxApiInfoValidation implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        ZbxApiInfo apiInfo = applicationContext.getBean(ZbxApiInfo.class);
        String response = apiInfo.getApiInfo();

        int version = NumberUtil.parseInt(response.replaceAll("\\.", ""));

        if (version < 540) {
            log.error("Zabbix API 当前版本为：{}，低于最低支持版本 5.4", response);
            int exitCode = SpringApplication.exit(applicationContext, () -> 0);
            System.exit(exitCode);
        }
    }
}

