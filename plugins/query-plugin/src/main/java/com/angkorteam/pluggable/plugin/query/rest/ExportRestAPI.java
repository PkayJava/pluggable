package com.angkorteam.pluggable.plugin.query.rest;

import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.database.JdbcTable;
import com.angkorteam.pluggable.framework.doc.ApiMethod;
import com.angkorteam.pluggable.framework.doc.ApiParam;
import com.angkorteam.pluggable.framework.rest.Controller;
import com.angkorteam.pluggable.framework.rest.RequestMapping;
import com.angkorteam.pluggable.framework.rest.RequestMethod;
import com.angkorteam.pluggable.framework.rest.Result;
import com.angkorteam.pluggable.framework.wicket.authroles.Role;
import com.angkorteam.pluggable.framework.wicket.authroles.Secured;
import com.angkorteam.pluggable.plugin.query.json.ExportQueryResponse;
import com.angkorteam.pluggable.plugin.query.json.ExportResultsResponse;
import com.angkorteam.pluggable.plugin.query.json.ExportTablesResponse;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.string.StringValue;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ExportRestAPI {

    @Secured(roles = {@Role(name = "ROLE_REST_QUERY_PLUGIN_EXPORT_TABLE", description = "Access Query Plugin Rest Export Table Name")})
    @RequestMapping(value = "/queryplugin/api/export/tables", method = RequestMethod.GET)
    @ApiMethod(description = "list all table name", responseDescription = "table name")
    public Result<ExportTablesResponse> tables(
            AbstractWebApplication application, WebRequest request,
            WebResponse response) throws JsonIOException, IOException {

        List<String> tables = new ArrayList<String>();

        for (JdbcTable jdbcTable : application.getSchema().allTables()) {
            tables.add(jdbcTable.getName());
        }

        ExportTablesResponse json = new ExportTablesResponse(200, null, tables);

        Gson gson = application.getBean(Gson.class);
        return Result.ok(response, gson, json);
    }

    @Secured(roles = {@Role(name = "ROLE_REST_QUERY_PLUGIN_EXPORT_QUERY", description = "Access Query Plugin Rest Export Query Data")})
    @RequestMapping(value = "/queryplugin/api/export/query", method = RequestMethod.POST)
    @ApiMethod(description = "export data for report", responseDescription = "result set")
    public Result<ExportQueryResponse> query(
            AbstractWebApplication application,
            WebRequest request,
            WebResponse response,
            @ApiParam(name = "query", description = "sql query") StringValue query)
            throws JsonIOException, IOException {
        JdbcTemplate jdbcTemplate = application.getBean(JdbcTemplate.class);

        List<Map<String, Object>> body = null;

        if ("".equals(query.toString(""))) {
            return Result.badRequest(response, "application/json",
                    ExportQueryResponse.class);
        }

        body = jdbcTemplate.queryForList(query.toOptionalString());

        Gson gson = application.getBean(Gson.class);
        ExportQueryResponse json = new ExportQueryResponse(200, null, body);
        return Result.ok(response, gson, json);
    }

    @Secured(roles = {@Role(name = "ROLE_REST_QUERY_PLUGIN_EXPORT_RESULT", description = "Access Query Plugin Rest Export Data Result")})
    @RequestMapping(value = "/queryplugin/api/export/result", method = RequestMethod.POST)
    @ApiMethod(description = "export data backup", responseDescription = "result set")
    public Result<ExportResultsResponse> results(
            AbstractWebApplication application,
            WebRequest request,
            WebResponse response,
            @ApiParam(name = "sort_field", type = String.class, description = "order by field asc") StringValue sort_field,
            @ApiParam(name = "first_result", type = Long.class) StringValue first_result,
            @ApiParam(name = "max_results", type = Long.class) StringValue max_results,
            @ApiParam(name = "table", type = String.class, description = "table name") StringValue table)
            throws JsonIOException, IOException {
        JdbcTemplate jdbcTemplate = application.getBean(JdbcTemplate.class);

        if ("".equals(table.toString(""))) {
            return Result.badRequest(response, "application/json",
                    ExportResultsResponse.class);
        }

        List<Map<String, Object>> body = null;
        if ("".equals(sort_field.toString(""))) {
            body = jdbcTemplate.queryForList("select * from " + table
                    + " limit " + first_result.toString("0") + ","
                    + max_results.toString("100"));
        } else {
            body = jdbcTemplate.queryForList("select * from " + table
                    + " order by " + sort_field.toOptionalString() + " limit "
                    + first_result.toString("0") + ","
                    + max_results.toString("100"));
        }
        Gson gson = application.getBean(Gson.class);
        ExportResultsResponse json = new ExportResultsResponse(200, null, body);
        return Result.ok(response, gson, json);
    }
}
