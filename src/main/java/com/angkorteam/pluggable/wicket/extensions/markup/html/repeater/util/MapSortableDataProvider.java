package com.angkorteam.pluggable.wicket.extensions.markup.html.repeater.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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

import com.angkorteam.pluggable.core.AbstractWebApplication;
import com.angkorteam.pluggable.database.ColumnMapRowMapper;

public class MapSortableDataProvider extends SortableDataProvider<Map<String, Object>, String> implements IFilterStateLocator<Map<String, Object>> {

    /**
     * 
     */
    private static final long serialVersionUID = 1698966044829981682L;

    private String query;

    private Map<String, Object> filter = new HashMap<String, Object>();

    private Map<String, Object> where = new HashMap<String, Object>();

    private List<String> group = new LinkedList<String>();

    private List<String> select = new LinkedList<String>();

    // private Map<String, Object> aggregate = new HashMap<String, Object>();

    public MapSortableDataProvider(String query) {
        this.query = query;
        this.select.add("*");
    }

    public void addWhere(String name, Object value) {
        // String lname = name.toLowerCase();
        // if ((lname.startsWith("avg(") || lname.startsWith("bit_and(") ||
        // lname.startsWith("bit_or(") || lname.startsWith("bit_xor(") ||
        // lname.startsWith("count(") || lname.startsWith("group_concat(") ||
        // lname.startsWith("max(") || lname.startsWith("min(") ||
        // lname.startsWith("std(") || lname.startsWith("sdtdev_pop(") ||
        // lname.startsWith("stddev_samp(") || lname.startsWith("stddev(") ||
        // lname.startsWith("sum(") || lname.startsWith("var_pop(") ||
        // lname.startsWith("var_samp(") || lname.startsWith("variance(") ||
        // lname.startsWith("avg (") || lname.startsWith("bit_and (") ||
        // lname.startsWith("bit_or (") || lname.startsWith("bit_xor (") ||
        // lname.startsWith("count (") || lname.startsWith("group_concat (") ||
        // lname.startsWith("max (") || lname.startsWith("min (") ||
        // lname.startsWith("std (")
        // || lname.startsWith("sdtdev_pop (") ||
        // lname.startsWith("stddev_samp (") || lname.startsWith("stddev (") ||
        // lname.startsWith("sum (") || lname.startsWith("var_pop (") ||
        // lname.startsWith("var_samp (") || lname.startsWith("variance ("))
        // && lname.endsWith(")")) {
        // this.aggregate.put(name, value);
        // } else {
        // }
        this.where.put(name, value);
    }

    public void addGroup(String name) {
        this.group.add(name);
    }

    public void addSelect(String name) {
        this.select.add(name);
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
        for (Entry<String, Object> entry : this.where.entrySet()) {
            if (entry.getValue() != null) {
                if (entry.getValue() instanceof String) {
                    if (!"".equals(entry.getValue())) {
                        where.add(entry.getKey() + " = :" + entry.getKey());
                        paramMap.put(entry.getKey(), entry.getValue());
                    }
                } else {
                    where.add(entry.getKey() + " = :" + entry.getKey());
                    paramMap.put(entry.getKey(), entry.getValue());
                }
            }
        }

        AbstractWebApplication application = (AbstractWebApplication) AbstractWebApplication.get();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        String groupBy = "";
        if (!this.group.isEmpty()) {
            groupBy = " group by " + StringUtils.join(this.group, ",") + " ";
        }
        if (where.isEmpty()) {
            if (getSort() == null) {
                return jdbcTemplate.query("select " + StringUtils.join(this.select, ",") + " from " + this.query + groupBy + " limit " + first + "," + count, new ColumnMapRowMapper()).listIterator();
            } else {
                return jdbcTemplate.query("select " + StringUtils.join(this.select, ",") + " from " + this.query + groupBy + " order by " + getSort().getProperty() + " " + (getSort().isAscending() ? "asc" : "desc") + " limit " + first + "," + count, new ColumnMapRowMapper()).listIterator();
            }
        } else {
            NamedParameterJdbcTemplate named = new NamedParameterJdbcTemplate(jdbcTemplate);
            if (getSort() == null) {
                return named.query("select " + StringUtils.join(this.select, ",") + " from " + this.query + " where " + StringUtils.join(where, " and ") + groupBy + " limit " + first + "," + count, paramMap, new ColumnMapRowMapper()).listIterator();
            } else {
                return named.query("select " + StringUtils.join(this.select, ",") + " from " + this.query + " where " + StringUtils.join(where, " and ") + groupBy + " order by " + getSort().getProperty() + " " + (getSort().isAscending() ? "asc" : "desc") + " limit " + first + "," + count, paramMap, new ColumnMapRowMapper()).listIterator();
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
        for (Entry<String, Object> entry : this.where.entrySet()) {
            if (entry.getValue() != null) {
                if (entry.getValue() instanceof String) {
                    if (!"".equals(entry.getValue())) {
                        where.add(entry.getKey() + " = :" + entry.getKey());
                        paramMap.put(entry.getKey(), entry.getValue());
                    }
                } else {
                    where.add(entry.getKey() + " = :" + entry.getKey());
                    paramMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        String groupBy = "";
        if (!this.group.isEmpty()) {
            groupBy = " group by " + StringUtils.join(this.group, ",") + " ";
        }
        AbstractWebApplication application = (AbstractWebApplication) AbstractWebApplication.get();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        if (where.isEmpty()) {
            if (this.group.isEmpty()) {
                return jdbcTemplate.queryForObject("select count(*) from " + this.query, Long.class);
            } else {
                return jdbcTemplate.queryForObject("select count(*) from (" + "select " + StringUtils.join(this.group, ",") + " from " + this.query + groupBy + ") pp", Long.class);
            }
        } else {
            NamedParameterJdbcTemplate named = new NamedParameterJdbcTemplate(jdbcTemplate);
            if (this.group.isEmpty()) {
                return named.queryForObject("select count(*) from " + this.query + " where " + StringUtils.join(where, " and "), paramMap, Long.class);
            } else {
                return named.queryForObject("select count(*) from (" + "select " + StringUtils.join(this.group, ",") + " from " + this.query + " where " + StringUtils.join(where, " and ") + groupBy + ") pp", paramMap, Long.class);
            }
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
