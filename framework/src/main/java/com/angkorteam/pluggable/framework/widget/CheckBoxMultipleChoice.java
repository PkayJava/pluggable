package com.angkorteam.pluggable.framework.widget;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.angkorteam.pluggable.framework.validation.type.Choice;
import com.angkorteam.pluggable.framework.validation.type.ChoiceType;

@Target({ FIELD })
@Retention(RUNTIME)
@Documented
public @interface CheckBoxMultipleChoice {

    String label();

    ChoiceType type() default ChoiceType.JAVA;

    String display() default "";

    Choice[] choices() default {};

    String where() default "";
    
    double order() default Double.MAX_VALUE;

}
