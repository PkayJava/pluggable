package com.angkorteam.pluggable.framework.doc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is to be used on your exposed methods.
 * 
 * @author Socheat KHAUV
 * 
 */
@Documented
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiMethod {

    /**
     * A description of what the method does
     * 
     * @return
     */
    String description() default "";

    ApiError[] errors() default {};

    ApiHeader[] headers() default {};

    String responseDescription() default "";

}
