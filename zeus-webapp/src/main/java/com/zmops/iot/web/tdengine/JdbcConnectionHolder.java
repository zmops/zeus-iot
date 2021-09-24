package com.zmops.iot.web.tdengine;

/**
 * jdbcConnection singleton
 **/
public enum JdbcConnectionHolder {

    INSTANCE;

    private JdbcConnection connection;

    public JdbcConnection getConnection() {
        return connection;
    }

    public void setConnection(JdbcConnection connection) {
        this.connection = connection;
    }
}
