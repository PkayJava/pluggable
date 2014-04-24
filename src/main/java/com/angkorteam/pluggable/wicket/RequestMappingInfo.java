package com.angkorteam.pluggable.wicket;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author Socheat KHAUV
 */
public class RequestMappingInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7929204713541587959L;

    private Class<?> clazz;

    private Method method;

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

}
