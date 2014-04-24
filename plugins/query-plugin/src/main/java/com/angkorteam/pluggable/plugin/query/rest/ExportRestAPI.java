package com.angkorteam.pluggable.plugin.query.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.jdbc.core.JdbcTemplate;

import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.database.Table;
import com.angkorteam.pluggable.framework.doc.ApiMethod;
import com.angkorteam.pluggable.framework.doc.ApiParam;
import com.angkorteam.pluggable.framework.rest.Controller;
import com.angkorteam.pluggable.framework.rest.RequestMapping;
import com.angkorteam.pluggable.framework.rest.RequestMethod;
import com.angkorteam.pluggable.framework.rest.Result;
import com.angkorteam.pluggable.framework.wicket.authroles.Role;
import com.angkorteam.pluggable.framework.wicket.authroles.Secured;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;

@Controller
public class ExportRestAPI {

    @Secured(roles = { @Role(name = "ROLE_REST_QUERY_PLUGIN_EXPORT_TABLE", description = "Access Query Plugin Rest Export Table Name") })
    @RequestMapping(value = "/queryplugin/api/export/tables", method = RequestMethod.GET)
    @ApiMethod(description = "list all table name", responseDescription = "table name", responseObject = String[].class)
    public Result tables(AbstractWebApplication application,
            HttpServletRequest request, HttpServletResponse response)
            throws JsonIOException, IOException {

        List<String> tables = new ArrayList<String>();

        for (Table table : application.getSchema().allTables()) {
            tables.add(table.getName());
        }

        Gson gson = application.getBean(Gson.class);
        gson.toJson(tables, response.getWriter());
        return Result.ok(response, "application/json");
    }

    @Secured(roles = { @Role(name = "ROLE_REST_QUERY_PLUGIN_EXPORT_QUERY", description = "Access Query Plugin Rest Export Query Data") })
    @RequestMapping(value = "/queryplugin/api/export/query", method = RequestMethod.POST)
    @ApiMethod(description = "export data for report", requestParameters = { @ApiParam(name = "query", description = "sql query") }, responseDescription = "result set", responseObject = Map[].class)
    public Result query(AbstractWebApplication application,
            HttpServletRequest request, HttpServletResponse response)
            throws JsonIOException, IOException {
        JdbcTemplate jdbcTemplate = application.getBean(JdbcTemplate.class);

        String query = request.getParameter("query");

        List<Map<String, Object>> body = null;

        if (query == null || "".equals(query)) {
            return Result.badRequest(response, "application/json");
        }

        body = jdbcTemplate.queryForList(query);

        Gson gson = application.getBean(Gson.class);
        gson.toJson(body, response.getWriter());
        return Result.ok(response, "application/json");
    }

    @Secured(roles = { @Role(name = "ROLE_REST_QUERY_PLUGIN_EXPORT_RESULT", description = "Access Query Plugin Rest Export Data Result") })
    @RequestMapping(value = "/queryplugin/api/export/result", method = RequestMethod.POST)
    @ApiMethod(description = "export data backup", requestParameters = {
            @ApiParam(name = "sortField", type = String.class, description = "order by field asc"),
            @ApiParam(name = "firstResult", type = Long.class),
            @ApiParam(name = "maxResults", type = Long.class),
            @ApiParam(name = "table", type = String.class, description = "table name") }, responseObject = Map[].class, responseDescription = "result set")
    public Result results(AbstractWebApplication application,
            HttpServletRequest request, HttpServletResponse response)
            throws JsonIOException, IOException {
        JdbcTemplate jdbcTemplate = application.getBean(JdbcTemplate.class);

        Long firstResult = 0l;
        try {
            firstResult = Long.valueOf(request.getParameter("firstResult"));
        } catch (NumberFormatException e) {
        }

        Long maxResults = 100l;
        try {
            maxResults = Long.valueOf(request.getParameter("maxResults"));
        } catch (NumberFormatException e) {
        }
        String table = request.getParameter("table");
        String sortField = request.getParameter("sortField");
        if (table == null || "".equals(table)) {
            return Result.badRequest(response, "application/json");
        }

        List<Map<String, Object>> body = null;
        if (sortField == null || "".equals(sortField)) {
            body = jdbcTemplate.queryForList("select * from " + table
                    + " limit " + firstResult + "," + maxResults);
        } else {
            body = jdbcTemplate.queryForList("select * from " + table
                    + " order by " + sortField + " limit " + firstResult + ","
                    + maxResults);
        }
        Gson gson = application.getBean(Gson.class);
        gson.toJson(body, response.getWriter());
        return Result.ok(response, "application/json");
    }
}
