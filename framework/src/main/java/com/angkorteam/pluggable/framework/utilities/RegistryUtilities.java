package com.angkorteam.pluggable.framework.utilities;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.angkorteam.pluggable.framework.database.EntityRowMapper;
import com.angkorteam.pluggable.framework.entity.ApplicationSetting;
import com.angkorteam.pluggable.framework.entity.PluginSetting;
import com.google.gson.Gson;

/**
 * @author Socheat KHAUV
 */
public class RegistryUtilities {

    private RegistryUtilities() {
    }

    public static final void update(Gson gson, JdbcTemplate jdbcTemplate, String identity, String name, Object value) {
        PluginSetting pluginSetting = null;
        try {
            pluginSetting = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(PluginSetting.class) + " where " + PluginSetting.IDENTITY + " = ? and " + PluginSetting.NAME + " = ?", new EntityRowMapper<PluginSetting>(PluginSetting.class), identity, name);
        } catch (EmptyResultDataAccessException e) {
        }
        if (pluginSetting != null) {
            jdbcTemplate.update("update " + TableUtilities.getTableName(PluginSetting.class) + " set value = ? where " + PluginSetting.IDENTITY + " = ? and " + PluginSetting.NAME + " = ?", gson.toJson(value), identity, name);
        } else {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
            insert.withTableName(TableUtilities.getTableName(PluginSetting.class));
            Map<String, Object> fields = new HashMap<String, Object>();
            fields.put(PluginSetting.IDENTITY, identity);
            fields.put(PluginSetting.NAME, name);
            fields.put(PluginSetting.VALUE, gson.toJson(value));
            insert.execute(fields);
        }
    }

    public static final <T> T select(JdbcTemplate jdbcTemplate, Gson gson, String identity, String name, Class<T> clazz) {
        PluginSetting pluginSetting = null;
        try {
            pluginSetting = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(PluginSetting.class) + " where " + PluginSetting.IDENTITY + " = ? and " + PluginSetting.NAME + " = ?", new EntityRowMapper<PluginSetting>(PluginSetting.class), identity, name);
        } catch (EmptyResultDataAccessException e) {
        }
        if (pluginSetting != null) {
            return gson.fromJson(pluginSetting.getValue(), clazz);
        }
        return null;
    }

    public static final void update(Gson gson, JdbcTemplate jdbcTemplate, String name, Object value) {
        ApplicationSetting applicationSetting = null;
        try {
            applicationSetting = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(ApplicationSetting.class) + " where " + ApplicationSetting.NAME + " = ?", new EntityRowMapper<ApplicationSetting>(ApplicationSetting.class), name);
        } catch (EmptyResultDataAccessException e) {
        }
        if (applicationSetting != null) {
            jdbcTemplate.update("update " + TableUtilities.getTableName(ApplicationSetting.class) + " set value = ? where " + ApplicationSetting.NAME + " = ?", gson.toJson(value), name);
        } else {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
            insert.withTableName(TableUtilities.getTableName(ApplicationSetting.class));
            Map<String, Object> fields = new HashMap<String, Object>();
            fields.put(ApplicationSetting.NAME, name);
            fields.put(ApplicationSetting.VALUE, gson.toJson(value));
            insert.execute(fields);
        }
    }

    public static final <T> T select(JdbcTemplate jdbcTemplate, Gson gson, String name, Class<T> clazz) {
        ApplicationSetting applicationSetting = null;
        try {
            applicationSetting = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(ApplicationSetting.class) + " where " + ApplicationSetting.NAME + " = ?", new EntityRowMapper<ApplicationSetting>(ApplicationSetting.class), name);
        } catch (EmptyResultDataAccessException e) {
        }
        if (applicationSetting != null) {
            return gson.fromJson(applicationSetting.getValue(), clazz);
        }
        return null;
    }
}
