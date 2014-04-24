package com.angkorteam.pluggable.framework.database.annotation;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Socheat KHAUV
 */
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {

    /**
     * (Optional) The name of the table.
     * <p>
     * Defaults to the entity name.
     */
    String name();

    /**
     * (Optional) The catalog of the table.
     * <p>
     * Defaults to the default catalog.
     */
    String catalog() default "";

    /**
     * (Optional) The schema of the table.
     * <p>
     * Defaults to the default schema for user.
     */
    String schema() default "";

}