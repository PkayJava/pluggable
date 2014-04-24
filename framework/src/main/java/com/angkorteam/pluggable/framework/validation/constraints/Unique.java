package com.angkorteam.pluggable.framework.validation.constraints;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * 
 * @author Socheat KHAUV
 */
@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RUNTIME)
@Documented
public @interface Unique {

    String where();

    Class<?> entity();

}
