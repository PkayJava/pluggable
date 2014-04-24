package com.angkorteam.pluggable.database.annotation;

import static com.angkorteam.pluggable.database.annotation.GenerationType.AUTO;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Socheat KHAUV
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Documented
public @interface GeneratedValue {

    /**
     * (Optional) The primary key generation strategy that the persistence
     * provider must use to generate the annotated entity primary key.
     */
    GenerationType strategy() default AUTO;

    /**
     * (Optional) The name of the primary key generator to use as specified in
     * the {@link SequenceGenerator} or {@link TableGenerator} annotation.
     * <p>
     * Defaults to the id generator supplied by persistence provider.
     */
    String generator() default "";
}
