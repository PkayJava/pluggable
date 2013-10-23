package com.itrustcambodia.pluggable.core;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Socheat KHAUV
 */
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mount {
    String value();
}
