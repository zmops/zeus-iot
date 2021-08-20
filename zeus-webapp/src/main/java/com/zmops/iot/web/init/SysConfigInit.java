package com.zmops.iot.web.init;

import com.zmops.iot.constant.ConstantsContext;
import com.zmops.iot.domain.sys.SysConfig;
import io.ebean.DB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author nantian created at 2021/8/1 20:50
 */

@Slf4j
@Component
public class SysConfigInit implements CommandLineRunner {

    @Override
    public void run(String... args) {

        //初始化所有的常量
        List<SysConfig> list = DB.find(SysConfig.class).findList();

        if (!list.isEmpty()) {
            for (SysConfig sysConfig : list) {
                ConstantsContext.putConstant(sysConfig.getCode(), sysConfig.getValue());
            }

            log.info("初始化常量" + list.size() + "条！");
        }

    }
}
