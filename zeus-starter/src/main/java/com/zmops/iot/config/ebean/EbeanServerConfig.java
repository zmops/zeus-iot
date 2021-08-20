package com.zmops.iot.config.ebean;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author nantian created at 2021/7/30 16:50
 * <p>
 * Ebean Server 配置
 */
@Configuration
public class EbeanServerConfig {


    @Autowired
    private DataSource dataSource;

    @Autowired
    private CurrentUser currentUser;

    @Bean
    public Database createEbeanDatabase() {
        DatabaseConfig config = new DatabaseConfig();
        config.setName("postgresql");

        config.add(new IdSnow());
        config.setDefaultServer(true);

        config.addPackage("com.zmops.iot.domain");

        config.setCurrentUserProvider(currentUser);
        config.setDataSource(dataSource);

        return DatabaseFactory.create(config);
    }
}
