package com.angkorteam.pluggable.framework.json;

import java.io.Serializable;

import com.angkorteam.pluggable.framework.doc.ApiObjectField;

public abstract class HttpMessage<T> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @ApiObjectField(description = "status of internal application, it might close to http status")
    protected int code;

    @ApiObjectField(description = "status of operation")
    protected String message;

    @ApiObjectField(description = "result of operation")
    protected T content;

    public HttpMessage(int code, String message, T content) {
        this.code = code;
        this.message = message;
        this.content = content;
    }

    public final int getCode() {
        return code;
    }

    public final void setCode(int code) {
        this.code = code;
    }

    public final String getMessage() {
        return message;
    }

    public final void setMessage(String message) {
        this.message = message;
    }

    public final T getContent() {
        return content;
    }

    public final void setContent(T content) {
        this.content = content;
    }

}
