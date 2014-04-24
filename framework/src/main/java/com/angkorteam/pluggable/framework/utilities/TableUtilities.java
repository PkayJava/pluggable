package com.angkorteam.pluggable.framework.utilities;

import java.lang.reflect.Field;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.angkorteam.pluggable.framework.database.DatabaseException;
import com.angkorteam.pluggable.framework.database.annotation.Column;
import com.angkorteam.pluggable.framework.database.annotation.Entity;
import com.angkorteam.pluggable.framework.database.annotation.Id;

/**
 * @author Socheat KHAUV
 */
public class TableUtilities {

    private TableUtilities() {
    }

    @SuppressWarnings("unchecked")
    public static final String getIdentityField(Class<?> clazz) {
        String id = "";
        for (Field field : org.reflections.ReflectionUtils.getAllFields(clazz)) {
            if (field.getAnnotation(Id.class) != null && field.getAnnotation(Column.class) != null) {
                id = field.getAnnotation(Column.class).name();
                break;
            }
        }
        return id;
    }

    @SuppressWarnings("unchecked")
    public static final String getIdentityValue(Object object) {
        Field id = null;
        for (Field field : org.reflections.ReflectionUtils.getAllFields(object.getClass())) {
            if (field.getAnnotation(Id.class) != null && field.getAnnotation(Column.class) != null) {
                id = field;
                break;
            }
        }
        if (id != null) {
            try {
                Object value = FieldUtils.readField(id, object, true);
                if (value != null) {
                    if (value instanceof String) {
                        return (String) value;
                    } else if (value instanceof Byte) {
                        return String.valueOf((Byte) value);
                    } else if (value instanceof Long) {
                        return String.valueOf((Long) value);
                    } else if (value instanceof Short) {
                        return String.valueOf((Short) value);
                    } else if (value instanceof Integer) {
                        return String.valueOf((Integer) value);
                    } else if (value instanceof Float) {
                        return String.valueOf((Float) value);
                    } else if (value instanceof Double) {
                        return String.valueOf((Double) value);
                    }
                }
            } catch (IllegalAccessException e) {
            }
        }
        return null;
    }

    public static final String getTableName(Class<?> clazz) {

        if (org.springframework.core.annotation.AnnotationUtils.findAnnotation(clazz, Entity.class) == null) {
            throw new DatabaseException(clazz.getSimpleName() + " is not an entity");
        }
        String name = clazz.getSimpleName();
        com.angkorteam.pluggable.framework.database.annotation.Table table = org.springframework.core.annotation.AnnotationUtils.findAnnotation(clazz, com.angkorteam.pluggable.framework.database.annotation.Table.class);
        if (table != null && table.name() != null && !"".equals(table.name())) {
            name = table.name();
        }
        return name;
    }

}
