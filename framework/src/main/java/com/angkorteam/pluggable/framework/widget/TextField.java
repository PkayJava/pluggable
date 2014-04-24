package com.angkorteam.pluggable.framework.widget;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.angkorteam.pluggable.framework.validation.type.TextFieldType;

@Target({ FIELD })
@Retention(RUNTIME)
@Documented
public @interface TextField {

    String label();

    String placeholder() default "";

    TextFieldType type() default TextFieldType.TEXT;

    String pattern() default "";

    double order() default Double.MAX_VALUE;

}
