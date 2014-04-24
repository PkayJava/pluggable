package com.angkorteam.pluggable.framework.widget;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ FIELD })
@Retention(RUNTIME)
@Documented
public @interface FileUploadField {

    String label();

    double order() default Double.MAX_VALUE;

}
