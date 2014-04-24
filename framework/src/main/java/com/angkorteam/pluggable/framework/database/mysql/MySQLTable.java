package com.angkorteam.pluggable.framework.database.mysql;

import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;

import com.angkorteam.pluggable.framework.database.DbSupport;
import com.angkorteam.pluggable.framework.database.Schema;
import com.angkorteam.pluggable.framework.database.Table;

/**
 * MySQL-specific table.
 */
public class MySQLTable extends Table {
    /**
     * Creates a new MySQL table.
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
    public MySQLTable(JdbcTemplate jdbcTemplate, DbSupport dbSupport, Schema schema, String name) {
        super(jdbcTemplate, dbSupport, schema, name);
    }

    @Override
    protected void doDrop() throws SQLException {
        jdbcTemplate.execute("DROP TABLE " + dbSupport.quote(schema.getName(), name));
    }

    @Override
    protected boolean doExists() throws SQLException {
        return exists(schema, null, name);
    }

    @Override
    protected boolean doExistsNoQuotes() throws SQLException {
        return exists(schema, null, name);
    }

    @Override
    protected void doLock() throws SQLException {
        jdbcTemplate.execute("SELECT * FROM " + this + " FOR UPDATE");
    }
}