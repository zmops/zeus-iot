package com.zmops.iot.web.tdengine;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JdbcConnectionInitializer implements InitializingBean {

    @Value("${taos.url}")
    public  String url;
    @Value("${taos.username}")
    public  String username;
    @Value("${taos.password}")
    public  String password;

    @Override
    public void afterPropertiesSet() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.taosdata.jdbc.TSDBDriver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        dataSource.setInitialSize(10);
        dataSource.setMinIdle(10);
        dataSource.setMaxActive(10);
        dataSource.setMaxWait(30000);
        dataSource.setValidationQuery("select server_status()");
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        JdbcConnectionHolder.INSTANCE.setConnection(new JdbcTemplateConnection(jdbcTemplate));
    }
}
