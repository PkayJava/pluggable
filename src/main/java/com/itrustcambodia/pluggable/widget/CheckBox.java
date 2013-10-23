package com.itrustcambodia.pluggable.widget;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ FIELD })
@Retention(RUNTIME)
@Documented
public @interface CheckBox {

    String label();

    String placeholder();

    double order() default Double.MAX_VALUE;

}
