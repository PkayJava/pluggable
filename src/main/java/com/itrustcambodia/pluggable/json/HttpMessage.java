package com.itrustcambodia.pluggable.json;

import java.io.Serializable;

public abstract class HttpMessage<T> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private int code;

    private String message;

    private T content;

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
