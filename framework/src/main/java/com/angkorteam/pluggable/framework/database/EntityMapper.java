package com.angkorteam.pluggable.framework.database;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.reflections.ReflectionUtils;
import org.springframework.jdbc.core.RowMapper;

import javax.persistence.*;


public abstract class EntityMapper<T> implements RowMapper<T> {

    private Class<T> clazz;

    protected EntityMapper(Class<T> clazz) {
        this.clazz = clazz;
        if (org.springframework.core.annotation.AnnotationUtils.findAnnotation(
                this.clazz, Entity.class) == null) {
            throw new DatabaseException(clazz.getSimpleName()
                    + " is not entity");
        }
    }
}
