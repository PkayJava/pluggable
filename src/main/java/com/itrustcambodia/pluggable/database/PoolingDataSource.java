package com.itrustcambodia.pluggable.database;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public class PoolingDataSource implements DataSource {

    private DataSource dataSource;

    private Connection connection;

    public PoolingDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return this.dataSource.getLogWriter();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return this.dataSource.getLoginTimeout();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.dataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        this.dataSource.setLoginTimeout(seconds);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.dataSource.isWrapperFor(iface);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.dataSource.unwrap(iface);
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            this.connection = this.dataSource.getConnection();
        }
        return this.connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = this.dataSource.getConnection(username, password);
        }
        return this.connection;
    }

}
