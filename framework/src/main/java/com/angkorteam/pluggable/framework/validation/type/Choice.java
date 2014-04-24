package com.angkorteam.pluggable.framework.validation.type;

import java.lang.annotation.ElementType;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * @author Socheat KHAUV
 */
@Target({ ElementType.METHOD })
@Retention(RUNTIME)
@Documented
public @interface Choice {

    String value();

    String display();

}
