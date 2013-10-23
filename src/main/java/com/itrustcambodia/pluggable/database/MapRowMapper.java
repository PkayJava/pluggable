package com.itrustcambodia.pluggable.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

public class MapRowMapper implements RowMapper<Map<String, String>> {

    @Override
    public Map<String, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, String> object = new HashMap<String, String>();
        ResultSetMetaData metaData = rs.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnLabel(i);
            object.put(columnName, rs.getString(i));
        }
        return object;
    }

}
