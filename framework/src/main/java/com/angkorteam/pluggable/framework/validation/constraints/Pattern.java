package com.angkorteam.pluggable.framework.validation.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The annotated {@code CharSequence} must match the specified regular
 * expression. The regular expression follows the Java regular expression
 * conventions see {@link java.util.regex.Pattern}.
 * <p/>
 * Accepts {@code CharSequence}. {@code null} elements are considered valid.
 * 
 * @author Socheat KHAUV
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Documented
public @interface Pattern {

    /**
     * @return the regular expression to match
     */
    String regexp();

}
