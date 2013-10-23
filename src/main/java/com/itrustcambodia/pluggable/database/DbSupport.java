package com.itrustcambodia.pluggable.database;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class DbSupport {
    /**
     * The JDBC template available for use.
     */
    protected JdbcTemplate jdbcTemplate;

    /**
     * Creates a new DbSupport instance with this JdbcTemplate.
     * 
     * @param jdbcTemplate
     *            The JDBC template to use.
     */
    public DbSupport(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Retrieves the schema with this name in the database.
     * 
     * @param name
     *            The name of the schema.
     * @return The schema.
     */
    public abstract Schema getSchema();

    /**
     * @return The database function that returns the current user.
     */
    public abstract String getCurrentUserFunction();

    /**
     * Checks whether ddl transactions are supported for this database.
     * 
     * @return {@code true} if ddl transactions are supported, {@code false} if
     *         not.
     */
    public abstract boolean supportsDdlTransactions();

    /**
     * @return The representation of the value {@code true} in a boolean column.
     */
    public abstract String getBooleanTrue();

    /**
     * @return The representation of the value {@code false} in a boolean
     *         column.
     */
    public abstract String getBooleanFalse();

    /**
     * Quote these identifiers for use in sql queries. Multiple identifiers will
     * be quoted and separated by a dot.
     * 
     * @param identifiers
     *            The identifiers to quote.
     * @return The fully qualified quoted identifiers.
     */
    public String quote(String... identifiers) {
        String result = "";

        boolean first = true;
        for (String identifier : identifiers) {
            if (!first) {
                result += ".";
            }
            first = false;
            result += doQuote(identifier);
        }

        return result;
    }

    /**
     * Quote this identifier for use in sql queries.
     * 
     * @param identifier
     *            The identifier to quote.
     * @return The fully qualified quoted identifier.
     */
    protected abstract String doQuote(String identifier);

    /**
     * @return {@code true} if this database use a catalog to represent a
     *         schema. {@code false} if a schema is simply a schema.
     */
    public abstract boolean catalogIsSchema();
}
