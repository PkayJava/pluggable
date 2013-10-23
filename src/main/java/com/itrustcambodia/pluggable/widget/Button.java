package com.itrustcambodia.pluggable.widget;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.itrustcambodia.pluggable.validation.type.ButtonType;

@Target({ METHOD })
@Retention(RUNTIME)
@Documented
public @interface Button {

    String label();

    boolean validate();

    ButtonType type() default ButtonType.DEFAULT;
    
    double order() default Double.MAX_VALUE;

}
