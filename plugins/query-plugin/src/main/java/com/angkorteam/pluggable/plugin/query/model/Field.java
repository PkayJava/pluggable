package com.angkorteam.pluggable.plugin.query.model;

import java.io.Serializable;

public class Field implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -436780428583262367L;

    private String name;

    private Object value;

    public Field() {
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

}
