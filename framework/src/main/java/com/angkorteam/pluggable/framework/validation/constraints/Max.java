package com.angkorteam.pluggable.framework.validation.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
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
 * @author Socheat KHAUV
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Documented
public @interface Max {

    /**
     * @return value the element must be lower or equal to
     */
    int value();

}
