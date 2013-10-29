package com.itrustcambodia.pluggable.wicket.model;

import java.util.Map;

import org.apache.wicket.model.IModel;

public class MapPropertyModel<T> implements IModel<T> {
    private static final long serialVersionUID = 1L;

    private final String expression;
    private Map<String, T> model;

    public MapPropertyModel(final Map<String, T> modelObject, final String expression) {
        this.expression = expression;
        this.model = modelObject;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(":expression=[").append(expression).append("]");
        return sb.toString();
    }

    @Override
    public void detach() {
    }

    @Override
    public T getObject() {
        return model.get(expression);
    }

    @Override
    public void setObject(T object) {
        this.model.put(expression, object);
    }

}