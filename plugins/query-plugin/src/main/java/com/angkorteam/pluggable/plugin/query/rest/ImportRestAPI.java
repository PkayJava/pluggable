package com.angkorteam.pluggable.plugin.query.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.string.StringValue;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.doc.ApiMethod;
import com.angkorteam.pluggable.framework.doc.ApiParam;
import com.angkorteam.pluggable.framework.rest.Controller;
import com.angkorteam.pluggable.framework.rest.RequestMapping;
import com.angkorteam.pluggable.framework.rest.RequestMethod;
import com.angkorteam.pluggable.framework.rest.Result;
import com.angkorteam.pluggable.framework.wicket.authroles.Role;
import com.angkorteam.pluggable.framework.wicket.authroles.Secured;
import com.angkorteam.pluggable.plugin.query.model.Field;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;

@Controller
public class ImportRestAPI {

    @Secured(roles = { @Role(name = "ROLE_REST_QUERY_PLUGIN_IMPORT", description = "Access Query Plugin Rest Import") })
    @RequestMapping(value = "/queryplugin/api/import", method = RequestMethod.POST)
    @ApiMethod(description = "import data")
    public Result<Void> importResult(
            AbstractWebApplication application,
            WebRequest request,
            WebResponse response,
            Gson gson,
            @ApiParam(name = "table", description = "table name", required = true, type = String.class) StringValue table,
            @ApiParam(name = "field", description = "field {'name':'value'}", required = true, type = String[].class) StringValue[] field)
            throws JsonIOException, IOException {

        if ("".equals(table.toString("")) || field == null || field.length == 0) {
            return Result.badRequest(response, "application/json", Void.class);
        }
        SimpleJdbcInsert insert = new SimpleJdbcInsert(
                application.getJdbcTemplate());
        insert.withTableName(table.toOptionalString());
        Map<String, Object> fields = new HashMap<String, Object>();
        for (StringValue f : field) {
            Field fi = gson.fromJson(f.toOptionalString(), Field.class);
            fields.put(fi.getName(), fi.getValue());
        }
        try {
            insert.execute(fields);
        } catch (DuplicateKeyException duplicateKeyException) {
            // LOGGER.info(duplicateKeyException.getMessage());
        }
        return Result.ok(response, "application/json", Void.class);
    }
}
