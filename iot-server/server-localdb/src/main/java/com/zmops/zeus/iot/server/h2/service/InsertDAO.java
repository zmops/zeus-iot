package com.zmops.zeus.iot.server.h2.service;

import com.zmops.zeus.iot.server.library.module.Service;

import java.sql.ResultSet;

/**
 * @author yefei
 **/
public interface InsertDAO extends Service {

    void insert(String sql);

    ResultSet queryRes(String sql);
}
