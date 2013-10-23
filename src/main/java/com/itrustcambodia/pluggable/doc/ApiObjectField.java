package com.itrustcambodia.pluggable.doc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is to be used on your objects' fields and represents a field
 * of an object
 * 
 * @author Socheat KHAUV
 * 
 */
@Documented
@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiObjectField {

    /**
     * A drescription of what the field is
     * 
     * @return
     */
    String description() default "";

    /**
     * The format pattern for this field
     * 
     * @return
     */
    String format() default "";

    /**
     * The allowed values for this field
     * 
     * @return
     */
    String[] allowedvalues() default {};

    boolean identity() default false;

}
