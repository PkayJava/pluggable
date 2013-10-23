package com.itrustcambodia.pluggable.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;

public abstract class Table extends SchemaObject {

    /**
     * Creates a new table.
     * 
     * @param jdbcTemplate
     *            The Jdbc Template for communicating with the DB.
     * @param dbSupport
     *            The database-specific support.
     * @param schema
     *            The schema this table lives in.
     * @param name
     *            The name of the table.
     */
    public Table(JdbcTemplate jdbcTemplate, DbSupport dbSupport, Schema schema, String name) {
        super(jdbcTemplate, dbSupport, schema, name);
    }

    /**
     * Checks whether this table exists.
     * 
     * @return {@code true} if it does, {@code false} if not.
     */
    public boolean exists() {
        try {
            return doExists();
        } catch (SQLException e) {
            throw new DatabaseException("Unable to check whether table " + this + " exists", e);
        }
    }

    /**
     * Checks whether this table exists.
     * 
     * @return {@code true} if it does, {@code false} if not.
     * @throws SQLException
     *             when the check failed.
     */
    protected abstract boolean doExists() throws SQLException;

    /**
     * Checks whether this table is already present in the database. WITHOUT
     * quoting either the table or the schema name!
     * 
     * @return {@code true} if the table exists, {@code false} if it doesn't.
     */
    public boolean existsNoQuotes() {
        try {
            return doExistsNoQuotes();
        } catch (SQLException e) {
            throw new DatabaseException("Unable to check whether table " + this + " exists", e);
        }
    }

    /**
     * Checks whether this table is already present in the database. WITHOUT
     * quoting either the table or the schema name!
     * 
     * @return {@code true} if the table exists, {@code false} if it doesn't.
     * @throws SQLException
     *             when there was an error checking whether this table exists in
     *             this schema.
     */
    protected abstract boolean doExistsNoQuotes() throws SQLException;

    /**
     * Checks whether the database contains a table matching these criteria.
     * 
     * @param catalog
     *            The catalog where the table resides. (optional)
     * @param schema
     *            The schema where the table resides. (optional)
     * @param table
     *            The name of the table. (optional)
     * @param tableTypes
     *            The types of table to look for (ex.: TABLE). (optional)
     * @return {@code true} if a matching table has been found, {@code false} if
     *         not.
     * @throws SQLException
     *             when the check failed.
     */
    protected boolean exists(Schema catalog, Schema schema, String table, String... tableTypes) throws SQLException {
        Connection connection = jdbcTemplate.getDataSource().getConnection();
        String[] types = tableTypes;
        if (types.length == 0) {
            types = null;
        }

        ResultSet resultSet = null;
        boolean found;
        try {
            resultSet = connection.getMetaData().getTables(catalog == null ? null : catalog.getName(), schema == null ? null : schema.getName(), table, types);
            found = resultSet.next();
        } finally {
            JdbcUtils.closeResultSet(resultSet);
            JdbcUtils.closeConnection(connection);
        }

        return found;
    }

    /**
     * Checks whether the table has a primary key.
     * 
     * @return {@code true} if a primary key has been found, {@code false} if
     *         not.
     */
    public boolean hasPrimaryKey() {
        ResultSet resultSet = null;
        boolean found;
        try {
            if (dbSupport.catalogIsSchema()) {
                resultSet = jdbcTemplate.getDataSource().getConnection().getMetaData().getPrimaryKeys(schema.getName(), null, name);
            } else {
                resultSet = jdbcTemplate.getDataSource().getConnection().getMetaData().getPrimaryKeys(null, schema.getName(), name);
            }
            found = resultSet.next();
        } catch (SQLException e) {
            throw new DatabaseException("Unable to check whether table " + this + " has a primary key", e);
        } finally {
            JdbcUtils.closeResultSet(resultSet);
        }

        return found;
    }

    /**
     * Checks whether the database contains a column matching these criteria.
     * 
     * @param column
     *            The column to look for.
     * @return {@code true} if a matching column has been found, {@code false}
     *         if not.
     */
    public boolean hasColumn(String column) {
        ResultSet resultSet = null;
        boolean found;
        try {
            if (dbSupport.catalogIsSchema()) {
                resultSet = jdbcTemplate.getDataSource().getConnection().getMetaData().getColumns(schema.getName(), null, name, column);
            } else {
                resultSet = jdbcTemplate.getDataSource().getConnection().getMetaData().getColumns(null, schema.getName(), name, column);
            }
            found = resultSet.next();
        } catch (SQLException e) {
            throw new DatabaseException("Unable to check whether table " + this + " has a column named " + column, e);
        } finally {
            JdbcUtils.closeResultSet(resultSet);
        }

        return found;
    }

    /**
     * Determines the size (in characters) of this column.
     * 
     * @param column
     *            The column to look for.
     * @return The size (in characters).
     */
    public int getColumnSize(String column) {
        ResultSet resultSet = null;
        int columnSize;
        try {
            if (dbSupport.catalogIsSchema()) {
                resultSet = jdbcTemplate.getDataSource().getConnection().getMetaData().getColumns(schema.getName(), null, name, column);
            } else {
                resultSet = jdbcTemplate.getDataSource().getConnection().getMetaData().getColumns(null, schema.getName(), name, column);
            }
            resultSet.next();
            columnSize = resultSet.getInt("COLUMN_SIZE");
        } catch (SQLException e) {
            throw new DatabaseException("Unable to check the size of column " + column + " in table " + this, e);
        } finally {
            JdbcUtils.closeResultSet(resultSet);
        }

        return columnSize;
    }

    /**
     * Locks this table in this schema using a read/write pessimistic lock until
     * the end of the current transaction.
     */
    public void lock() {
        try {
            // LOG.debug("Locking table " + this + "...");
            doLock();
            // LOG.debug("Lock acquired for table " + this);
        } catch (SQLException e) {
            throw new DatabaseException("Unable to lock table " + this, e);
        }
    }

    /**
     * Locks this table in this schema using a read/write pessimistic lock until
     * the end of the current transaction.
     * 
     * @throws SQLException
     *             when this table in this schema could not be locked.
     */
    protected abstract void doLock() throws SQLException;
}