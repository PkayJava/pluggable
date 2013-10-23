package com.itrustcambodia.pluggable.doc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.lang3.ObjectUtils.Null;

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

    Class<?> requestObject() default Null.class;

    ApiParam[] requestParameters() default {};

    ApiHeader[] headers() default {};

    ApiError[] errors() default {};

    Class<?> responseObject();

    String responseDescription() default "";

}
