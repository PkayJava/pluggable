package com.angkorteam.pluggable.framework.mapper;

import com.angkorteam.pluggable.framework.database.DatabaseException;
import org.reflections.ReflectionUtils;
import org.springframework.jdbc.core.RowMapper;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class GenericEntityMapper<T> implements RowMapper<T> {

    private Class<T> clazz;

    private Map<String, String> fields = new HashMap<String, String>();

    private Map<String, Field> types = new HashMap<String, Field>();

    @SuppressWarnings("unchecked")
    public GenericEntityMapper(Class<T> clazz) {
        this.clazz = clazz;
        if (org.springframework.core.annotation.AnnotationUtils.findAnnotation(
                this.clazz, Entity.class) == null) {
            throw new DatabaseException(clazz.getSimpleName()
                    + " is not entity");
        }
        for (Field field : ReflectionUtils.getAllFields(this.clazz)) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                fields.put(column.name(), field.getName());
                types.put(field.getName(), field);
            }
        }
    }

    /**
* get rid of synchronized and get rid of static MAPPER
*/
    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        T entity = null;
        try {
            entity = clazz.newInstance();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }
        ResultSetMetaData metaData = rs.getMetaData();
        if (entity != null) {
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                Field field = types.get(fields.get(columnName));
                if (field == null) {
                    continue;
                }
                if (metaData.getColumnType(i) == Types.ARRAY) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "ARRAY");
                } else if (metaData.getColumnType(i) == Types.BIGINT) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "BIGINT");
                } else if (metaData.getColumnType(i) == Types.BINARY) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "BINARY");
                } else if (metaData.getColumnType(i) == Types.BIT) {
                    if (field.getType().getName().equals("boolean")
                            || field.getType().getName()
                                    .equals("java.lang.Boolean")) {
                        try {
                            field.setAccessible(true);
                            field.set(entity, rs.getBoolean(i));
                            field.setAccessible(false);
                        } catch (IllegalArgumentException e) {
                        } catch (IllegalAccessException e) {
                        }
                    } else {
                        throw new DatabaseException("DB Error "
                                + field.getType().getName() + " " + "BIT");
                    }
                } else if (metaData.getColumnType(i) == Types.BLOB) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "BLOB");
                } else if (metaData.getColumnType(i) == Types.BOOLEAN) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "BOOLEAN");
                } else if (metaData.getColumnType(i) == Types.CHAR) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "CHAR");
                } else if (metaData.getColumnType(i) == Types.CLOB) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "CLOB");
                } else if (metaData.getColumnType(i) == Types.DATALINK) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "DATALINK");
                } else if (metaData.getColumnType(i) == Types.DATE) {
                    if (field.getType().getName().equals("java.util.Date")) {
                        try {
                            field.setAccessible(true);
                            field.set(entity, rs.getDate(i));
                            field.setAccessible(false);
                        } catch (IllegalArgumentException e) {
                        } catch (IllegalAccessException e) {
                        }
                    } else {
                        throw new DatabaseException("DB Error "
                                + field.getType().getName() + " " + "DATE");
                    }
                } else if (metaData.getColumnType(i) == Types.DECIMAL) {
                    if (field.getType().getName().equals("double")
                            || field.getType().getName()
                                    .equals("java.lang.Double")) {
                        try {
                            field.setAccessible(true);
                            field.set(entity, rs.getDouble(i));
                            field.setAccessible(false);
                        } catch (IllegalArgumentException e) {
                        } catch (IllegalAccessException e) {
                        }
                    } else {
                        throw new DatabaseException("DB Error "
                                + field.getType().getName() + " " + "DECIMAL");
                    }
                } else if (metaData.getColumnType(i) == Types.DISTINCT) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "DISTINCT");
                } else if (metaData.getColumnType(i) == Types.DOUBLE) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "DOUBLE");
                } else if (metaData.getColumnType(i) == Types.FLOAT) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "FLOAT");
                } else if (metaData.getColumnType(i) == Types.INTEGER) {
                    if (field.getType().getName().equals("java.lang.Long")
                            || field.getType().getName().equals("long")) {
                        try {
                            field.setAccessible(true);
                            field.set(entity, rs.getLong(i));
                            field.setAccessible(false);
                        } catch (IllegalArgumentException e) {
                        } catch (IllegalAccessException e) {
                        }
                    } else if (field.getType().getName()
                            .equals("java.lang.Integer")
                            || field.getType().getName().equals("int")) {
                        try {
                            field.setAccessible(true);
                            field.set(entity, rs.getInt(i));
                            field.setAccessible(false);
                        } catch (IllegalArgumentException e) {
                        } catch (IllegalAccessException e) {
                        }
                    } else {
                        throw new DatabaseException("DB Error "
                                + field.getType().getName() + " " + "INTEGER");
                    }
                } else if (metaData.getColumnType(i) == Types.JAVA_OBJECT) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "JAVA_OBJECT");
                } else if (metaData.getColumnType(i) == Types.LONGNVARCHAR) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "LONGNVARCHAR");
                } else if (metaData.getColumnType(i) == Types.LONGVARBINARY) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "LONGVARBINARY");
                } else if (metaData.getColumnType(i) == Types.LONGVARCHAR) {
                    if (field.getType().getName().equals("java.lang.String")) {
                        try {
                            field.setAccessible(true);
                            field.set(entity, rs.getString(i));
                            field.setAccessible(false);
                        } catch (IllegalArgumentException e) {
                        } catch (IllegalAccessException e) {
                        }
                    } else {
                        throw new DatabaseException("DB Error "
                                + field.getType().getName() + " "
                                + "LONGVARCHAR");
                    }
                } else if (metaData.getColumnType(i) == Types.NCHAR) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "NCHAR");
                } else if (metaData.getColumnType(i) == Types.NCLOB) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "NCLOB");
                } else if (metaData.getColumnType(i) == Types.NULL) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "NULL");
                } else if (metaData.getColumnType(i) == Types.NUMERIC) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "NUMERIC");
                } else if (metaData.getColumnType(i) == Types.NVARCHAR) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "NVARCHAR");
                } else if (metaData.getColumnType(i) == Types.OTHER) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "OTHER");
                } else if (metaData.getColumnType(i) == Types.REAL) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "REAL");
                } else if (metaData.getColumnType(i) == Types.REF) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "REF");
                } else if (metaData.getColumnType(i) == Types.ROWID) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "ROWID");
                } else if (metaData.getColumnType(i) == Types.SMALLINT) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "SMALLINT");
                } else if (metaData.getColumnType(i) == Types.SQLXML) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "SQLXML");
                } else if (metaData.getColumnType(i) == Types.STRUCT) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "STRUCT");
                } else if (metaData.getColumnType(i) == Types.TIME) {
                    if (field.getType().getName().equals("java.util.Date")) {
                        try {
                            field.setAccessible(true);
                            field.set(entity, rs.getTime(i));
                            field.setAccessible(false);
                        } catch (IllegalArgumentException e) {
                        } catch (IllegalAccessException e) {
                        }
                    } else {
                        throw new DatabaseException("DB Error "
                                + field.getType().getName() + " " + "TIME");
                    }
                } else if (metaData.getColumnType(i) == Types.TIMESTAMP) {
                    if (field.getType().getName().equals("java.util.Date")) {
                        try {
                            field.setAccessible(true);
                            field.set(entity, rs.getTimestamp(i));
                            field.setAccessible(false);
                        } catch (IllegalArgumentException e) {
                        } catch (IllegalAccessException e) {
                        }
                    } else {
                        throw new DatabaseException("DB Error "
                                + field.getType().getName() + " " + "TIMESTAMP");
                    }
                } else if (metaData.getColumnType(i) == Types.TINYINT) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "TINYINT");
                } else if (metaData.getColumnType(i) == Types.VARBINARY) {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "VARBINARY");
                } else if (metaData.getColumnType(i) == Types.VARCHAR) {
                    if (field.getType().getName().equals("java.lang.String")) {
                        try {
                            field.setAccessible(true);
                            field.set(entity, rs.getString(i));
                            field.setAccessible(false);
                        } catch (IllegalArgumentException e) {
                        } catch (IllegalAccessException e) {
                        }
                    } else {
                        throw new DatabaseException("DB Error "
                                + field.getType().getName() + " " + "VARCHAR");
                    }
                } else {
                    throw new DatabaseException("DB Error "
                            + field.getType().getName() + " " + "UNKNOW");
                }
            }
        }
        return entity;
    }
}