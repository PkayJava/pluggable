package com.angkorteam.pluggable.framework.database;

import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class SchemaObject {
    /**
     * The Jdbc Template for communicating with the DB.
     */
    protected JdbcTemplate jdbcTemplate;

    /**
     * The database-specific support.
     */
    protected DbSupport dbSupport;

    /**
     * The schema this table lives in.
     */
    protected final Schema schema;

    /**
     * The name of the table.
     */
    protected final String name;

    /**
     * Creates a new schema object with this name within this schema.
     * 
     * @param jdbcTemplate
     *            The jdbc template to access the DB.
     * @param dbSupport
     *            The database-specific support.
     * @param schema
     *            The schema the object lives in.
     * @param name
     *            The name of the object.
     */
    public SchemaObject(JdbcTemplate jdbcTemplate, DbSupport dbSupport, Schema schema, String name) {
        this.name = name;
        this.jdbcTemplate = jdbcTemplate;
        this.dbSupport = dbSupport;
        this.schema = schema;
    }

    /**
     * @return The schema this object lives in.
     */
    public final Schema getSchema() {
        return schema;
    }

    /**
     * @return The name of the object.
     */
    public final String getName() {
        return name;
    }

    /**
     * Drops this object from the database.
     */
    public final void drop() {
        try {
            doDrop();
        } catch (SQLException e) {
            throw new DatabaseException("Unable to drop " + this, e);
        }
    }

    /**
     * Drops this object from the database.
     * 
     * @throws java.sql.SQLException
     *             when the drop failed.
     */
    protected abstract void doDrop() throws SQLException;

    @Override
    public String toString() {
        return dbSupport.quote(schema.getName(), name);
    }
}
