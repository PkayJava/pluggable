package com.itrustcambodia.pluggable.validation.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The annotated element must not be {@code null}. Accepts any type.
 * 
 * @author Emmanuel Bernard
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Documented
public @interface NotNull {

}
