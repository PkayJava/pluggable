package com.angkorteam.pluggable.framework.doc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is to be used on your object classes and represents an object
 * used for communication between clients and server
 * 
 * @author Socheat KHAUV
 * 
 */
@Documented
@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiObject {

    String description() default "";

}
