package com.angkorteam.pluggable.framework.database.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation specifies that the property or field is not persistent. It is
 * used to annotate a property or field of an entity class, mapped superclass,
 * or embeddable class.
 * 
 * <pre>
 *    Example:
 *    &#064;Entity
 *    public class Employee {
 *        &#064;Id int id;
 *        &#064;Transient User currentUser;
 *        ...
 *    }
 * </pre>
 * 
 * @author Socheat KHAUV
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Documented
public @interface Transient {
}