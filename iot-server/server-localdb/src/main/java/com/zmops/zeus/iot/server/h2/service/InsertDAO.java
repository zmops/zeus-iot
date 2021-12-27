package com.zmops.zeus.iot.server.h2.service;

import com.zmops.zeus.server.library.module.Service;

import java.sql.ResultSet;

/**
 * @author yefei
 **/
public interface InsertDAO extends Service {

    void insert(String sql);

    int update(String sql, Object... params);

    ResultSet queryRes(String sql, Object... params);

    void delete(String sql);
}
