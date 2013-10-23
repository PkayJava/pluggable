package com.itrustcambodia.pluggable.core;

import java.util.Map;

import org.apache.wicket.model.IDetachable;
import org.apache.wicket.model.IModel;

public class MapModel<T> implements IModel<T> {

    /**
     * 
     */
    private static final long serialVersionUID = 2545716394928280233L;

    private Map<String, Object> object;

    private String key;

    public MapModel(Map<String, Object> object, String key) {
        this.object = object;
        this.key = key;
    }

    @Override
    public void detach() {
        Object object = this.object.get(key);
        if (object instanceof IDetachable) {
            ((IDetachable) object).detach();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() {
        return (T) this.object.get(key);
    }

    @Override
    public void setObject(T object) {
        this.object.put(key, object);
    }

}
