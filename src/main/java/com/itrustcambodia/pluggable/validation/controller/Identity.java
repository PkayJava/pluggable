package com.itrustcambodia.pluggable.validation.controller;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Socheat KHAUV
 */

@Target({ FIELD })
@Retention(RUNTIME)
@Documented
public @interface Identity {
}
