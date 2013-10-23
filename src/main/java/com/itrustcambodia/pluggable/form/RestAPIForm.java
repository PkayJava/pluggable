package com.itrustcambodia.pluggable.form;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.itrustcambodia.pluggable.rest.RequestMethod;

public class RestAPIForm implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1358062404348563511L;

    private String path;

    private String description;

    private boolean deprecated;

    private RequestMethod method;

    private String[] roles;

    private List<List<Map<String, String>>> urlParameters;

    private List<List<Map<String, String>>> formParameters;

    private List<Map<String, String>> headers;

    private List<Map<String, String>> errors;

    private String requestObject;

    private String responseObject;

    private String responseDescription;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public void setMethod(RequestMethod method) {
        this.method = method;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public List<List<Map<String, String>>> getUrlParameters() {
        return urlParameters;
    }

    public void setUrlParameters(List<List<Map<String, String>>> urlParameters) {
        this.urlParameters = urlParameters;
    }

    public List<List<Map<String, String>>> getFormParameters() {
        return formParameters;
    }

    public void setFormParameters(List<List<Map<String, String>>> formParameters) {
        this.formParameters = formParameters;
    }

    public List<Map<String, String>> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Map<String, String>> headers) {
        this.headers = headers;
    }

    public String getRequestObject() {
        return requestObject;
    }

    public void setRequestObject(String requestObject) {
        this.requestObject = requestObject;
    }

    public String getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(String responseObject) {
        this.responseObject = responseObject;
    }

    public List<Map<String, String>> getErrors() {
        return errors;
    }

    public void setErrors(List<Map<String, String>> errors) {
        this.errors = errors;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

}
