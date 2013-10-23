package com.itrustcambodia.pluggable.validation.constraints;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The annotated element must be a number whose value must be lower or equal to
 * the specified maximum.
 * <p/>
 * Supported types are:
 * <ul>
 * <li>{@code BigDecimal}</li>
 * <li>{@code BigInteger}</li>
 * <li>{@code byte}, {@code short}, {@code int}, {@code long}, and their
 * respective wrappers</li>
 * </ul>
 * Note that {@code double} and {@code float} are not supported due to rounding
 * errors (some providers might provide some approximative support).
 * <p/>
 * {@code null} elements are considered valid.
 * 
 * @author Emmanuel Bernard
 */
@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RUNTIME)
@Documented
public @interface Unique {

    String where();

    Class<?> entity();

}
