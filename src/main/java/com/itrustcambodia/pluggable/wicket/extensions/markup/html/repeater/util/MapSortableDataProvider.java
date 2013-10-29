package com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.MapModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.database.ColumnMapRowMapper;

public class MapSortableDataProvider extends SortableDataProvider<Map<String, Object>, String> implements IFilterStateLocator<Map<String, Object>> {

    /**
     * 
     */
    private static final long serialVersionUID = 1698966044829981682L;

    private String query;

    private Map<String, Object> filter = new HashMap<String, Object>();

    public MapSortableDataProvider(String query) {
        this.query = query;
    }

    @Override
    public Iterator<? extends Map<String, Object>> iterator(long first, long count) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        List<String> where = new ArrayList<String>();
        for (Entry<String, Object> entry : filter.entrySet()) {
            if (entry.getValue() != null) {
                if (entry.getValue() instanceof String) {
                    if (!"".equals(entry.getValue())) {
                        where.add(entry.getKey() + " like :" + entry.getKey());
                        paramMap.put(entry.getKey(), entry.getValue() + "%");
                    }
                } else {
                    where.add(entry.getKey() + " = :" + entry.getKey());
                    paramMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        AbstractWebApplication application = (AbstractWebApplication) AbstractWebApplication.get();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        if (where.isEmpty()) {
            if (getSort() == null) {
                return jdbcTemplate.query("select * from " + this.query, new ColumnMapRowMapper()).listIterator();
            } else {
                return jdbcTemplate.query("select * from " + this.query + " order by " + getSort().getProperty() + " " + (getSort().isAscending() ? "asc" : "desc"), new ColumnMapRowMapper()).listIterator();
            }
        } else {
            NamedParameterJdbcTemplate named = new NamedParameterJdbcTemplate(jdbcTemplate);
            if (getSort() == null) {
                return named.query("select * from " + this.query + " where " + StringUtils.join(where, " and "), paramMap, new ColumnMapRowMapper()).listIterator();
            } else {
                return named.query("select * from " + this.query + " where " + StringUtils.join(where, " and ") + " order by " + getSort().getProperty() + " " + (getSort().isAscending() ? "asc" : "desc"), paramMap, new ColumnMapRowMapper()).listIterator();
            }
        }
    }

    @Override
    public long size() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        List<String> where = new ArrayList<String>();
        for (Entry<String, Object> entry : filter.entrySet()) {
            if (entry.getValue() != null) {
                if (entry.getValue() instanceof String) {
                    if (!"".equals(entry.getValue())) {
                        where.add(entry.getKey() + " like :" + entry.getKey());
                        paramMap.put(entry.getKey(), entry.getValue() + "%");
                    }
                } else {
                    where.add(entry.getKey() + " = :" + entry.getKey());
                    paramMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        AbstractWebApplication application = (AbstractWebApplication) AbstractWebApplication.get();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        if (where.isEmpty()) {
            return jdbcTemplate.queryForObject("select count(*) from " + this.query, Long.class);
        } else {
            NamedParameterJdbcTemplate named = new NamedParameterJdbcTemplate(jdbcTemplate);
            return named.queryForObject("select count(*) from " + this.query + " where " + StringUtils.join(where, " and "), paramMap, Long.class);
        }
    }

    @Override
    public IModel<Map<String, Object>> model(Map<String, Object> object) {
        MapModel<String, Object> model = new MapModel<String, Object>(object);
        return model;
    }

    @Override
    public Map<String, Object> getFilterState() {
        return this.filter;
    }

    @Override
    public void setFilterState(Map<String, Object> state) {
        this.filter = state;
    }

}
