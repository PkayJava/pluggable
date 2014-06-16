package com.angkorteam.pluggable.framework.rest;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.wicket.protocol.http.servlet.ResponseIOException;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.http.WebResponse.CacheScope;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;

/**
 * @author Socheat KHAUV
 */
public class Result<T> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1733741221268184786L;

    private static final Logger LOGGER = LoggerFactory.getLogger(Result.class);

    private int status;

    private String contentType;

    private Result() {
    }

    private Result(WebResponse response, int status) {
        this.status = status;
        response.setStatus(status);
    }

    private Result(HttpServletResponse response, int status) {
        this.status = status;
        response.setStatus(status);
    }

    private Result(WebResponse response, int status, String contentType) {
        this.status = status;
        this.contentType = contentType;
        response.setStatus(status);
        response.setContentType(contentType);
    }

    private Result(HttpServletResponse response, int status, String contentType) {
        this.status = status;
        this.contentType = contentType;
        response.setStatus(status);
        response.setContentType(contentType);
    }

    public static final <T> Result<T> ok(WebResponse response) {
        Result<T> result = new Result<T>(response, 200);
        return result;
    }

    public static final <T> Result<T> ok(HttpServletResponse response) {
        Result<T> result = new Result<T>(response, 200);
        return result;
    }

    public static final <T> Result<T> fake(Class<T> clazz) {
        return new Result<T>();
    }

    public static final <T> Result<T> fake() {
        return new Result<T>();
    }

    public static final <T> Result<T> ok(AbstractWebApplication application,
            File file, WebResponse response) throws IOException {
        response.enableCaching(Duration.ONE_WEEK, CacheScope.PUBLIC);
        response.setLastModifiedTime(Time.valueOf(new Date(file.lastModified())));
        String extension = FilenameUtils.getExtension(file.getAbsolutePath());
        String mine = application.lookupMineType(extension);
        if (mine != null && !"".equals(mine)) {
            response.setContentType(mine);
        }
        response.addHeader("Content-Disposition", "attachment; filename=\n"
                + file.getName() + "\"");
        response.setContentLength(file.length());
        FileInputStream inputStream = new FileInputStream(file);
        try {
            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (EOFException e) {
            LOGGER.info("error {}", e.getMessage());
        } catch (ResponseIOException e) {
            LOGGER.info("error {}", e.getMessage());
        }
        IOUtils.closeQuietly(inputStream);
        return Result.<T> fake();
    }

    public static final <T> Result<T> ok(AbstractWebApplication application,
            File file, HttpServletResponse response) throws IOException {
        String extension = FilenameUtils.getExtension(file.getAbsolutePath());
        String mine = application.lookupMineType(extension);
        if (mine != null && !"".equals(mine)) {
            response.setContentType(mine);
        }
        response.addHeader("Content-Disposition", "attachment; filename=\n"
                + file.getName() + "\"");
        response.setContentLength(Long.valueOf(file.length()).intValue());
        FileInputStream inputStream = new FileInputStream(file);
        try {
            IOUtils.copy(inputStream, response.getOutputStream());
        } catch (EOFException e) {
            System.out.println(e.getMessage());
        } catch (Throwable e) {
            System.out.println(e.getClass().getName());
        }
        IOUtils.closeQuietly(inputStream);
        return Result.<T> fake();
    }

    public static final <T> Result<T> ok(WebResponse response,
            String contentType, Class<T> clazz) {
        Result<T> result = new Result<T>(response, 200, contentType);
        return result;
    }

    public static final <T> Result<T> ok(HttpServletResponse response,
            String contentType, Class<T> clazz) {
        Result<T> result = new Result<T>(response, 200, contentType);
        return result;
    }

    public static final <T> Result<T> ok(WebResponse response, Gson gson,
            T content) throws JsonIOException, IOException {
        Result<T> result = new Result<T>(response, 200, "application/json");
        response.disableCaching();
        HttpServletResponse httpServletResponse = (HttpServletResponse) response
                .getContainerResponse();
        httpServletResponse.setCharacterEncoding("UTF-8");
        gson.toJson(content, httpServletResponse.getWriter());
        return result;
    }

    public static final <T> Result<T> ok(HttpServletResponse response,
            Gson gson, T content) throws JsonIOException, IOException {
        Result<T> result = new Result<T>(response, 200, "application/json");
        response.setCharacterEncoding("UTF-8");
        gson.toJson(content, response.getWriter());
        return result;
    }

    public static final <T> Result<T> created(WebResponse response,
            Class<T> clazz) {
        return new Result<T>(response, 201);
    }

    public static final <T> Result<T> created(HttpServletResponse response,
            Class<T> clazz) {
        return new Result<T>(response, 201);
    }

    public static final <T> Result<T> created(WebResponse response,
            String contentType, Class<T> clazz) {
        return new Result<T>(response, 201, contentType);
    }

    public static final <T> Result<T> created(HttpServletResponse response,
            String contentType, Class<T> clazz) {
        return new Result<T>(response, 201, contentType);
    }

    public static final <T> Result<T> accepted(WebResponse response,
            Class<T> clazz) {
        return new Result<T>(response, 202);
    }

    public static final <T> Result<T> accepted(HttpServletResponse response,
            Class<T> clazz) {
        return new Result<T>(response, 202);
    }

    public static final <T> Result<T> accepted(WebResponse response,
            String contentType, Class<T> clazz) {
        return new Result<T>(response, 202, contentType);
    }

    public static final <T> Result<T> accepted(HttpServletResponse response,
            String contentType, Class<T> clazz) {
        return new Result<T>(response, 202, contentType);
    }

    public static final <T> Result<T> found(WebResponse response, Class<T> clazz) {
        return new Result<T>(response, 302);
    }

    public static final <T> Result<T> found(HttpServletResponse response,
            Class<T> clazz) {
        return new Result<T>(response, 302);
    }

    public static final <T> Result<T> found(WebResponse response,
            String contentType, Class<T> clazz) {
        return new Result<T>(response, 302, contentType);
    }

    public static final <T> Result<T> found(HttpServletResponse response,
            String contentType, Class<T> clazz) {
        return new Result<T>(response, 302, contentType);
    }

    public static final <T> Result<T> badRequest(WebResponse response,
            Class<T> clazz) {
        return new Result<T>(response, 400);
    }

    public static final <T> Result<T> badRequest(HttpServletResponse response,
            Class<T> clazz) {
        return new Result<T>(response, 400);
    }

    public static final <T> Result<T> badRequest(WebResponse response,
            String contentType, Class<T> clazz) {
        return new Result<T>(response, 400, contentType);
    }

    public static final <T> Result<T> badRequest(HttpServletResponse response,
            String contentType, Class<T> clazz) {
        return new Result<T>(response, 400, contentType);
    }

    public static final <T> Result<T> unauthorized(WebResponse response,
            Class<T> clazz) {
        return new Result<T>(response, 401);
    }

    public static final <T> Result<T> unauthorized(
            HttpServletResponse response, Class<T> clazz) {
        return new Result<T>(response, 401);
    }

    public static final <T> Result<T> unauthorized(WebResponse response,
            String contentType, Class<T> clazz) {
        return new Result<T>(response, 401, contentType);
    }

    public static final <T> Result<T> unauthorized(
            HttpServletResponse response, String contentType, Class<T> clazz) {
        return new Result<T>(response, 401, contentType);
    }

    public static final <T> Result<T> forbidden(WebResponse response,
            Class<T> clazz) {
        return new Result<T>(response, 403);
    }

    public static final <T> Result<T> forbidden(HttpServletResponse response,
            Class<T> clazz) {
        return new Result<T>(response, 403);
    }

    public static final <T> Result<T> forbidden(WebResponse response,
            String contentType, Class<T> clazz) {
        return new Result<T>(response, 403, contentType);
    }

    public static final <T> Result<T> forbidden(HttpServletResponse response,
            String contentType, Class<T> clazz) {
        return new Result<T>(response, 403, contentType);
    }

    public static final <T> Result<T> notFound(WebResponse response,
            Class<T> clazz) {
        return new Result<T>(response, 404);
    }

    public static final <T> Result<T> notFound(HttpServletResponse response,
            Class<T> clazz) {
        return new Result<T>(response, 404);
    }

    public static final <T> Result<T> notFound(WebResponse response,
            String contentType, Class<T> clazz) {
        return new Result<T>(response, 404, contentType);
    }

    public static final <T> Result<T> notFound(HttpServletResponse response,
            String contentType, Class<T> clazz) {
        return new Result<T>(response, 404, contentType);
    }

    public static final <T> Result<T> gone(WebResponse response, Class<T> clazz) {
        return new Result<T>(response, 410);
    }

    public static final <T> Result<T> gone(HttpServletResponse response,
            Class<T> clazz) {
        return new Result<T>(response, 410);
    }

    public static final <T> Result<T> gone(WebResponse response,
            String contentType, Class<T> clazz) {
        return new Result<T>(response, 410, contentType);
    }

    public static final <T> Result<T> gone(HttpServletResponse response,
            String contentType, Class<T> clazz) {
        return new Result<T>(response, 410, contentType);
    }

    public static final <T> Result<T> locked(WebResponse response,
            Class<T> clazz) {
        return new Result<T>(response, 423);
    }

    public static final <T> Result<T> locked(HttpServletResponse response,
            Class<T> clazz) {
        return new Result<T>(response, 423);
    }

    public static final <T> Result<T> locked(WebResponse response,
            String contentType, Class<T> clazz) {
        return new Result<T>(response, 423, contentType);
    }

    public static final <T> Result<T> locked(HttpServletResponse response,
            String contentType, Class<T> clazz) {
        return new Result<T>(response, 423, contentType);
    }

    public static final <T> Result<T> internalServerError(WebResponse response,
            Class<T> clazz) {
        return new Result<T>(response, 500);
    }

    public static final <T> Result<T> internalServerError(
            HttpServletResponse response, Class<T> clazz) {
        return new Result<T>(response, 500);
    }

    public static final <T> Result<T> internalServerError(WebResponse response,
            String contentType, Class<T> clazz) {
        return new Result<T>(response, 500, contentType);
    }

    public static final <T> Result<T> internalServerError(
            HttpServletResponse response, String contentType, Class<T> clazz) {
        return new Result<T>(response, 500, contentType);
    }

    public static final <T> Result<T> notImplemented(WebResponse response,
            Class<T> clazz) {
        return new Result<T>(response, 501);
    }

    public static final <T> Result<T> notImplemented(
            HttpServletResponse response, Class<T> clazz) {
        return new Result<T>(response, 501);
    }

    public static final <T> Result<T> notImplemented(WebResponse response,
            String contentType, Class<T> clazz) {
        return new Result<T>(response, 501, contentType);
    }

    public static final <T> Result<T> notImplemented(
            HttpServletResponse response, String contentType, Class<T> clazz) {
        return new Result<T>(response, 501, contentType);
    }

    public static final <T> Result<T> serviceUnavailable(WebResponse response,
            Class<T> clazz) {
        return new Result<T>(response, 503);
    }

    public static final <T> Result<T> serviceUnavailable(
            HttpServletResponse response, Class<T> clazz) {
        return new Result<T>(response, 503);
    }

    public static final <T> Result<T> serviceUnavailable(WebResponse response,
            String contentType, Class<T> clazz) {
        return new Result<T>(response, 503, contentType);
    }

    public static final <T> Result<T> serviceUnavailable(
            HttpServletResponse response, String contentType, Class<T> clazz) {
        return new Result<T>(response, 503, contentType);
    }

    public int getStatus() {
        return status;
    }

    public String getContentType() {
        return contentType;
    }

}
