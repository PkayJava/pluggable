package com.angkorteam.pluggable.database.annotation;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation defines a primary key generator that may 
 * be referenced by name when a generator element is specified 
 * for the {@link GeneratedValue} annotation. A sequence generator 
 * may be specified on the entity class or on the primary key 
 * field or property. The scope of the generator name is global 
 * to the persistence unit (across all generator types).
 *
 * <pre>
 *   Example:
 *
 *   &#064;SequenceGenerator(name="EMP_SEQ", allocationSize=25)
 * </pre>
 *
 * @author Socheat KHAUV
 */
@Target({TYPE, METHOD, FIELD}) 
@Retention(RUNTIME)

public @interface SequenceGenerator {

    /** 
     * (Required) A unique generator name that can be referenced 
     * by one or more classes to be the generator for primary key 
     * values.
     */
    String name();

    /**
     * (Optional) The name of the database sequence object from 
     * which to obtain primary key values.
     * <p> Defaults to a provider-chosen value.
     */
    String sequenceName() default "";

    /** 
     * (Optional) The value from which the sequence object 
     * is to start generating.
     */
    int initialValue() default 1;

    /**
     * (Optional) The amount to increment by when allocating 
     * sequence numbers from the sequence.
     */
    int allocationSize() default 50;
}
