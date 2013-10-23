package com.itrustcambodia.pluggable.database.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Specifies that the class is an entity. This annotation is applied to the
 * entity class.
 * 
 * @since Java Persistence 1.0
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Entity {

    /**
     * The name of an entity. Defaults to the unqualified name of the entity
     * class. This name is used to refer to the entity in queries. The name must
     * not be a reserved literal in the Java Persistence query language.
     */
    String name() default "";
}
