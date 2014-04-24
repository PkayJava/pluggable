package com.angkorteam.pluggable.plugin.query.model;

import java.io.Serializable;

public class Field implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -436780428583262367L;

    private String name;

    private Object value;

    private String clazz;

    public Field() {
    }

    public Field(String name, Object value, String clazz) {
        super();
        this.name = name;
        this.value = value;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }
}
