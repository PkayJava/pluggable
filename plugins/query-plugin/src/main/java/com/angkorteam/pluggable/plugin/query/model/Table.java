package com.angkorteam.pluggable.plugin.query.model;

import java.io.Serializable;

public class Table implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 4755560026163854018L;

    private String name;

    private Field[] fields;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Field[] getFields() {
        return fields;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }
}
