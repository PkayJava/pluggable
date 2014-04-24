package com.angkorteam.pluggable.validation.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The annotated element must be a number within accepted range Supported types
 * are:
 * <ul>
 * <li>{@code BigDecimal}</li>
 * <li>{@code BigInteger}</li>
 * <li>{@code CharSequence}</li>
 * <li>{@code byte}, {@code short}, {@code int}, {@code long}, and their
 * respective wrapper types</li>
 * </ul>
 * <p/>
 * {@code null} elements are considered valid.
 * 
 * @author Socheat KHAUV
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Documented
public @interface Digits {

    /**
     * @return maximum number of integral digits accepted for this number
     */
    int integer();

    /**
     * @return maximum number of fractional digits accepted for this number
     */
    int fraction();

}
