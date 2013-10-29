package com.itrustcambodia.pluggable.widget;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ METHOD })
@Retention(RUNTIME)
@Documented
public @interface Link {

    String label();

    double order() default Double.MAX_VALUE;

}
