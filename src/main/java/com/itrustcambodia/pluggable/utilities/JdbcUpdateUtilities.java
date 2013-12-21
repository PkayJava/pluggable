package com.itrustcambodia.pluggable.utilities;

import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.jdbc.SimpleJdbcUpdate;
import com.itrustcambodia.pluggable.utilities.TableUtilities;

public class JdbcUpdateUtilities {

    public static void execute(JdbcTemplate jdbcTemplate, Class<?> entity,
            Map<String, Object> fields, Map<String, Object> wheres) {
        SimpleJdbcUpdate update = new SimpleJdbcUpdate(jdbcTemplate);
        update.withTableName(TableUtilities.getTableName(entity));

        update.usingColumns(fields.keySet().toArray(
                new String[fields.keySet().size()]));
        update.whereColumns(wheres.keySet().toArray(
                new String[wheres.keySet().size()]));

        update.execute(fields, wheres);
    }
}
