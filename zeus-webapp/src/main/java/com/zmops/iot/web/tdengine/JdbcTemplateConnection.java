package com.zmops.iot.web.tdengine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * 只读的jdbcTemplate
 *
 * @author yefei
 */
@Slf4j
public class JdbcTemplateConnection implements JdbcConnection {

    private JdbcTemplate jdbcTemplate;

    public JdbcTemplateConnection(JdbcTemplate jdbcTemplate) {
        Assert.state(jdbcTemplate != null, "jdbcTemplate could not be null");
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql) throws DataAccessException {
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public void execute(String sql) throws DataAccessException {
        jdbcTemplate.execute(sql);
    }
}
