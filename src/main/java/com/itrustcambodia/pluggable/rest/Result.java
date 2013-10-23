package com.itrustcambodia.pluggable.rest;

import java.io.Serializable;

/**
 * @author Socheat KHAUV
 */
public class Result implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1733741221268184786L;

    private int status;

    private String contentType;

    private Result(int status) {
        this.status = status;
    }

    private Result(int status, String contentType) {
        this.status = status;
        this.contentType = contentType;
    }

    public static final Result ok() {
        return new Result(200);
    }

    public static final Result ok(String contentType) {
        return new Result(200, contentType);
    }

    public static final Result created() {
        return new Result(201);
    }

    public static final Result created(String contentType) {
        return new Result(201, contentType);
    }

    public static final Result accepted() {
        return new Result(202);
    }

    public static final Result accepted(String contentType) {
        return new Result(202, contentType);
    }

    public static final Result found() {
        return new Result(302);
    }

    public static final Result found(String contentType) {
        return new Result(302, contentType);
    }

    public static final Result badRequest() {
        return new Result(400);
    }

    public static final Result badRequest(String contentType) {
        return new Result(400, contentType);
    }

    public static final Result unauthorized() {
        return new Result(401);
    }

    public static final Result unauthorized(String contentType) {
        return new Result(401, contentType);
    }

    public static final Result forbidden() {
        return new Result(403);
    }

    public static final Result forbidden(String contentType) {
        return new Result(403, contentType);
    }

    public static final Result notFound() {
        return new Result(404);
    }

    public static final Result notFound(String contentType) {
        return new Result(404, contentType);
    }

    public static final Result gone() {
        return new Result(410);
    }

    public static final Result gone(String contentType) {
        return new Result(410, contentType);
    }

    public static final Result locked() {
        return new Result(423);
    }

    public static final Result locked(String contentType) {
        return new Result(423, contentType);
    }

    public static final Result internalServerError() {
        return new Result(500);
    }

    public static final Result internalServerError(String contentType) {
        return new Result(500, contentType);
    }

    public static final Result notImplemented() {
        return new Result(501);
    }

    public static final Result notImplemented(String contentType) {
        return new Result(501, contentType);
    }

    public static final Result serviceUnavailable() {
        return new Result(503);
    }

    public static final Result serviceUnavailable(String contentType) {
        return new Result(503, contentType);
    }

    public int getStatus() {
        return status;
    }

    public String getContentType() {
        return contentType;
    }

}
