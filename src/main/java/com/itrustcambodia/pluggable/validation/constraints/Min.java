package com.itrustcambodia.pluggable.validation.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The annotated element must be a number whose value must be higher or equal to
 * the specified minimum.
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
@Target({ FIELD })
@Retention(RUNTIME)
@Documented
public @interface Min {

    /**
     * @return value the element must be higher or equal to
     */
    int value();

}
