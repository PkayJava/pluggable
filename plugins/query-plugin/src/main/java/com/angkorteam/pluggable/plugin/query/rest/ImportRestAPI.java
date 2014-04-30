package com.angkorteam.pluggable.plugin.query.rest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.doc.ApiMethod;
import com.angkorteam.pluggable.framework.rest.Controller;
import com.angkorteam.pluggable.framework.rest.RequestMapping;
import com.angkorteam.pluggable.framework.rest.RequestMethod;
import com.angkorteam.pluggable.framework.rest.Result;
import com.angkorteam.pluggable.framework.wicket.authroles.Role;
import com.angkorteam.pluggable.framework.wicket.authroles.Secured;
import com.angkorteam.pluggable.plugin.query.model.Field;
import com.angkorteam.pluggable.plugin.query.model.Table;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;

@Controller
public class ImportRestAPI {

    // private static final Logger LOGGER = LoggerFactory
    // .getLogger(ImportRestAPI.class);

    @Secured(roles = { @Role(name = "ROLE_REST_QUERY_PLUGIN_IMPORT", description = "Access Query Plugin Rest Import") })
    @RequestMapping(value = "/queryplugin/api/import", method = RequestMethod.POST)
    @ApiMethod(description = "import data", requestObject = Table.class)
    public Result<Void> importResult(AbstractWebApplication application,
            WebRequest request, WebResponse response) throws JsonIOException,
            IOException {
        Gson gson = application.getGson();

        InputStreamReader streamReader = new InputStreamReader(
                ((HttpServletRequest) request.getContainerRequest())
                        .getInputStream(),
                "UTF-8");

        Table table = gson.fromJson(streamReader, Table.class);
        if (table == null || table.getName() == null
                || "".equals(table.getName()) || table.getFields() == null
                || table.getFields().length == 0) {
            return Result.badRequest(response, "application/json", Void.class);
        }
        SimpleJdbcInsert insert = new SimpleJdbcInsert(
                application.getJdbcTemplate());
        insert.withTableName(table.getName());
        Map<String, Object> fields = new HashMap<String, Object>();
        for (Field field : table.getFields()) {
            fields.put(field.getName(), field.getValue());
        }
        try {
            insert.execute(fields);
        } catch (DuplicateKeyException duplicateKeyException) {
            // LOGGER.info(duplicateKeyException.getMessage());
        }
        return Result.ok(response, "application/json", Void.class);
    }

    @Secured(roles = { @Role(name = "ROLE_REST_QUERY_PLUGIN_IMPORT_BATCH", description = "Access Query Plugin Rest Import Batch") })
    @RequestMapping(value = "/queryplugin/api/import/batch", method = RequestMethod.POST)
    @ApiMethod(description = "import data", requestObject = Table[].class)
    public Result<Void> importBatch(AbstractWebApplication application,
            WebRequest request, WebResponse response) throws JsonIOException,
            IOException {
        Gson gson = application.getGson();

        InputStreamReader streamReader = new InputStreamReader(
                ((HttpServletRequest) request.getContainerRequest())
                        .getInputStream(),
                "UTF-8");

        Table[] tables = gson.fromJson(streamReader, Table[].class);
        if (tables == null || tables.length == 0) {
            return Result.badRequest(response, "application/json", Void.class);
        } else {
            for (Table table : tables) {
                if (table == null || table.getName() == null
                        || "".equals(table.getName())
                        || table.getFields() == null
                        || table.getFields().length == 0) {
                    return Result.badRequest(response, "application/json",
                            Void.class);
                }
            }
        }

        for (Table table : tables) {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(
                    application.getJdbcTemplate());
            insert.withTableName(table.getName());
            Map<String, Object> fields = new HashMap<String, Object>();
            for (Field field : table.getFields()) {
                fields.put(field.getName(), field.getValue());
            }
            try {
                insert.execute(fields);
            } catch (DuplicateKeyException duplicateKeyException) {
                // LOGGER.info(duplicateKeyException.getMessage());
            }
        }

        return Result.ok(response, "application/json", Void.class);
    }

}
