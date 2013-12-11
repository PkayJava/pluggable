package com.itrustcambodia.pluggable.json;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Socheat KHAUV
 */
public class ObjectAPIForm implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1358062404348563511L;

    private String name;

    private String description;

    private boolean deprecated;

    private List<List<Map<String, String>>> fields;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<List<Map<String, String>>> getFields() {
        return fields;
    }

    public void setFields(List<List<Map<String, String>>> fields) {
        this.fields = fields;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

}
