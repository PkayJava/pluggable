package com.angkorteam.pluggable.framework.database.mysql;

import org.springframework.jdbc.core.JdbcTemplate;

import com.angkorteam.pluggable.framework.database.DbSupport;
import com.angkorteam.pluggable.framework.database.Schema;

public class MySQLDbSupport extends DbSupport {

    public MySQLDbSupport(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public Schema getSchema() {
        String name = jdbcTemplate.queryForObject("select SCHEMA()", String.class);
        return new MySQLSchema(jdbcTemplate, this, name);
    }

    @Override
    public String getCurrentUserFunction() {
        return "SUBSTRING_INDEX(USER(),'@',1)";
    }

    @Override
    public boolean supportsDdlTransactions() {
        return false;
    }

    @Override
    public String getBooleanTrue() {
        return "1";
    }

    @Override
    public String getBooleanFalse() {
        return "0";
    }

    @Override
    protected String doQuote(String identifier) {
        return "`" + identifier + "`";
    }

    @Override
    public boolean catalogIsSchema() {
        return true;
    }

}
