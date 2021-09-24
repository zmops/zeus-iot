package com.zmops.iot.web.tdengine;

import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Map;

public interface JdbcConnection {


    List<Map<String, Object>> queryForList(String sql) throws DataAccessException;


    void execute(final String sql) throws DataAccessException;
}
