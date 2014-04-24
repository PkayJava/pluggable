package com.angkorteam.pluggable.validation.constraints;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The annotated element must be a date in the future. Now is defined as the
 * current time according to the virtual machine The calendar used if the
 * compared type is of type {@code Calendar} is the calendar based on the
 * current timezone and the current locale.
 * <p/>
 * Supported types are:
 * <ul>
 * <li>{@code java.util.Date}</li>
 * </ul>
 * <p/>
 * {@code null} elements are considered valid.
 * 
 * @author Socheat KHAUV
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Documented
public @interface Future {

}
