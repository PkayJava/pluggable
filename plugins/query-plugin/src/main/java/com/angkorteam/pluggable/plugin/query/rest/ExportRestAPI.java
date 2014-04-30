package com.angkorteam.pluggable.plugin.query.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
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
import com.angkorteam.pluggable.plugin.query.json.ExportQueryResponse;
import com.angkorteam.pluggable.plugin.query.json.ExportResultsResponse;
import com.angkorteam.pluggable.plugin.query.json.ExportTablesResponse;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;

@Controller
public class ExportRestAPI {

    @Secured(roles = { @Role(name = "ROLE_REST_QUERY_PLUGIN_EXPORT_TABLE", description = "Access Query Plugin Rest Export Table Name") })
    @RequestMapping(value = "/queryplugin/api/export/tables", method = RequestMethod.GET)
    @ApiMethod(description = "list all table name", responseDescription = "table name")
    public Result<ExportTablesResponse> tables(
            AbstractWebApplication application, WebRequest request,
            WebResponse response) throws JsonIOException, IOException {

        List<String> tables = new ArrayList<String>();

        for (Table table : application.getSchema().allTables()) {
            tables.add(table.getName());
        }

        ExportTablesResponse json = new ExportTablesResponse(200, null, tables);

        Gson gson = application.getBean(Gson.class);
        return Result.ok(response, gson, json);
    }

    @Secured(roles = { @Role(name = "ROLE_REST_QUERY_PLUGIN_EXPORT_QUERY", description = "Access Query Plugin Rest Export Query Data") })
    @RequestMapping(value = "/queryplugin/api/export/query", method = RequestMethod.POST)
    @ApiMethod(description = "export data for report", requestParameters = { @ApiParam(name = "query", description = "sql query") }, responseDescription = "result set")
    public Result<ExportQueryResponse> query(
            AbstractWebApplication application, WebRequest request,
            WebResponse response) throws JsonIOException, IOException {
        JdbcTemplate jdbcTemplate = application.getBean(JdbcTemplate.class);

        String query = request.getRequestParameters()
                .getParameterValue("query").toOptionalString();

        List<Map<String, Object>> body = null;

        if (query == null || "".equals(query)) {
            return Result.badRequest(response, "application/json",
                    ExportQueryResponse.class);
        }

        body = jdbcTemplate.queryForList(query);

        Gson gson = application.getBean(Gson.class);
        ExportQueryResponse json = new ExportQueryResponse(200, null, body);
        return Result.ok(response, gson, json);
    }

    @Secured(roles = { @Role(name = "ROLE_REST_QUERY_PLUGIN_EXPORT_RESULT", description = "Access Query Plugin Rest Export Data Result") })
    @RequestMapping(value = "/queryplugin/api/export/result", method = RequestMethod.POST)
    @ApiMethod(description = "export data backup", requestParameters = {
            @ApiParam(name = "sortField", type = String.class, description = "order by field asc"),
            @ApiParam(name = "firstResult", type = Long.class),
            @ApiParam(name = "maxResults", type = Long.class),
            @ApiParam(name = "table", type = String.class, description = "table name") }, responseDescription = "result set")
    public Result<ExportResultsResponse> results(
            AbstractWebApplication application, WebRequest request,
            WebResponse response) throws JsonIOException, IOException {
        JdbcTemplate jdbcTemplate = application.getBean(JdbcTemplate.class);

        Long firstResult = request.getRequestParameters()
                .getParameterValue("firstResult").toLong(0l);

        Long maxResults = request.getRequestParameters()
                .getParameterValue("maxResults").toLong(100l);

        String table = request.getRequestParameters()
                .getParameterValue("table").toOptionalString();
        String sortField = request.getRequestParameters()
                .getParameterValue("sortField").toOptionalString();
        if (table == null || "".equals(table)) {
            return Result.badRequest(response, "application/json",
                    ExportResultsResponse.class);
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
        ExportResultsResponse json = new ExportResultsResponse(200, null, body);
        return Result.ok(response, gson, json);
    }
}
