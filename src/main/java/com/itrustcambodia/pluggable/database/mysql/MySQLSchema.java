package com.itrustcambodia.pluggable.database.mysql;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.database.DbSupport;
import com.itrustcambodia.pluggable.database.Schema;
import com.itrustcambodia.pluggable.database.Table;
import com.itrustcambodia.pluggable.database.annotation.Column;
import com.itrustcambodia.pluggable.database.annotation.GeneratedValue;
import com.itrustcambodia.pluggable.database.annotation.GenerationType;
import com.itrustcambodia.pluggable.database.annotation.Id;
import com.itrustcambodia.pluggable.database.annotation.Unique;
import com.itrustcambodia.pluggable.utilities.TableUtilities;

/**
 * MySQL implementation of Schema.
 */
public class MySQLSchema extends Schema {
    /**
     * Creates a new MySQL schema.
     * 
     * @param jdbcTemplate
     *            The Jdbc Template for communicating with the DB.
     * @param dbSupport
     *            The database-specific support.
     * @param name
     *            The name of the schema.
     */
    public MySQLSchema(JdbcTemplate jdbcTemplate, DbSupport dbSupport, String name) {
        super(jdbcTemplate, dbSupport, name);
    }

    @Override
    protected boolean doExists() throws SQLException {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name=?", int.class, name) > 0;
    }

    @Override
    protected boolean doEmpty() throws SQLException {
        int objectCount = jdbcTemplate.queryForObject("Select " + "(Select count(*) from information_schema.TABLES Where TABLE_SCHEMA=?) + " + "(Select count(*) from information_schema.VIEWS Where TABLE_SCHEMA=?) + " + "(Select count(*) from information_schema.TABLE_CONSTRAINTS Where TABLE_SCHEMA=?) + " + "(Select count(*) from information_schema.ROUTINES Where ROUTINE_SCHEMA=?)", int.class, name, name, name, name);
        return objectCount == 0;
    }

    @Override
    protected void doCreate() throws SQLException {
        jdbcTemplate.execute("CREATE SCHEMA " + dbSupport.quote(name));
    }

    @Override
    protected void doDrop() throws SQLException {
        jdbcTemplate.execute("DROP SCHEMA " + dbSupport.quote(name));
    }

    @Override
    protected void doClean() throws SQLException {
        for (String statement : cleanRoutines()) {
            jdbcTemplate.execute(statement);
        }

        for (String statement : cleanViews()) {
            jdbcTemplate.execute(statement);
        }

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        for (Table table : allTables()) {
            table.drop();
        }
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    /**
     * Generate the statements to clean the routines in this schema.
     * 
     * @return The list of statements.
     * @throws SQLException
     *             when the clean statements could not be generated.
     */
    private List<String> cleanRoutines() throws SQLException {
        List<Map<String, Object>> routineNames = jdbcTemplate.queryForList("SELECT routine_name, routine_type FROM information_schema.routines WHERE routine_schema=?", name);

        List<String> statements = new ArrayList<String>();
        for (Map<String, Object> row : routineNames) {
            String routineName = (String) row.get("routine_name");
            String routineType = (String) row.get("routine_type");
            statements.add("DROP " + routineType + " " + dbSupport.quote(name, routineName));
        }
        return statements;
    }

    /**
     * Generate the statements to clean the views in this schema.
     * 
     * @return The list of statements.
     * @throws SQLException
     *             when the clean statements could not be generated.
     */
    private List<String> cleanViews() throws SQLException {
        List<String> viewNames = jdbcTemplate.queryForList("SELECT table_name FROM information_schema.views WHERE table_schema=?", String.class, name);

        List<String> statements = new ArrayList<String>();
        for (String viewName : viewNames) {
            statements.add("DROP VIEW " + dbSupport.quote(name, viewName));
        }
        return statements;
    }

    @Override
    protected Table[] doAllTables() throws SQLException {
        List<String> tableNames = jdbcTemplate.queryForList("SELECT table_name FROM information_schema.tables WHERE table_schema=? AND table_type='BASE TABLE'", String.class, name);

        Table[] tables = new Table[tableNames.size()];
        for (int i = 0; i < tableNames.size(); i++) {
            tables[i] = new MySQLTable(jdbcTemplate, dbSupport, this, tableNames.get(i));
        }
        return tables;
    }

    @Override
    public Table getTable(String tableName) {
        return new MySQLTable(jdbcTemplate, dbSupport, this, tableName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void createTable(Class<?> entity) {
        List<String> columns = new ArrayList<String>();
        for (Field field : org.reflections.ReflectionUtils.getAllFields(entity)) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                columns.add(column.name());
            }
        }

        createTable(entity, columns);

    }

    @Override
    public void createTable(Class<?> entity, String... fields) {
        createTable(entity, Arrays.asList(fields));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void createTable(Class<?> entity, List<String> fields) {

        String name = TableUtilities.getTableName(entity);

        List<String> ids = new ArrayList<String>();
        List<String> columns = new ArrayList<String>();
        List<String> idColumns = new ArrayList<String>();
        Map<String, List<String>> uniqueGroups = new HashMap<String, List<String>>();

        for (Field field : org.reflections.ReflectionUtils.getAllFields(entity)) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                if (fields.contains(column.name())) {
                    Id id = field.getAnnotation(Id.class);
                    GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
                    Unique unique = field.getAnnotation(Unique.class);
                    String ddl = "";

                    if (unique != null) {
                        if (unique.group() != null && !"".equals(unique.group())) {
                            if (!uniqueGroups.containsKey(unique.group())) {
                                uniqueGroups.put(unique.group(), new ArrayList<String>());
                            }
                            List<String> group = uniqueGroups.get(unique.group());
                            group.add(column.name());
                            ddl = column.name() + " " + column.columnDefinition() + (column.nullable() ? "" : " NOT NULL");
                        } else {
                            ddl = column.name() + " " + column.columnDefinition() + " UNIQUE" + (column.nullable() ? "" : " NOT NULL");
                        }
                    } else {
                        ddl = column.name() + " " + column.columnDefinition() + (column.nullable() ? "" : " NOT NULL");
                    }

                    if (generatedValue != null && generatedValue.strategy() == GenerationType.IDENTITY) {
                        ddl = ddl + " AUTO_INCREMENT";
                    }
                    if (id != null) {
                        idColumns.add(column.name());
                        ids.add(ddl);
                    } else {
                        columns.add(ddl);
                    }

                }
            }
        }

        List<String> field = new LinkedList<String>();
        if (ids != null && !ids.isEmpty()) {
            field.addAll(ids);
        }
        field.addAll(columns);
        if (uniqueGroups != null && !uniqueGroups.isEmpty()) {
            for (Entry<String, List<String>> item : uniqueGroups.entrySet()) {
                if (item.getValue() != null && !item.getValue().isEmpty()) {
                    field.add("UNIQUE (" + StringUtils.join(item.getValue(), ",") + ")");
                }
            }
        }
        if (idColumns != null && !idColumns.isEmpty()) {
            field.add("PRIMARY KEY (" + StringUtils.join(idColumns, ",") + ")");
        }

        jdbcTemplate.execute("CREATE TABLE " + name + "(" + StringUtils.join(field, ",") + ")");
    }

    @Override
    public Table getTable(Class<?> clazz) {
        return getTable(TableUtilities.getTableName(clazz));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addColumn(Class<?> entity, String name) {
        String tableName = TableUtilities.getTableName(entity);
        Field field = null;
        for (Field tmp : org.reflections.ReflectionUtils.getAllFields(entity)) {
            if (tmp.getName().equals(name)) {
                field = tmp;
                break;
            }
        }
        if (field != null) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                jdbcTemplate.execute("ALTER TABLE " + tableName + " ADD COLUMN " + column.name() + " " + column.columnDefinition());
            }
        }
    }
}