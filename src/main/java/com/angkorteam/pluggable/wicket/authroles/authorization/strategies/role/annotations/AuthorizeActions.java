package com.angkorteam.pluggable.wicket.authroles.authorization.strategies.role.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Socheat KHAUV
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
@Inherited
public @interface AuthorizeActions {

    /**
     * The actions that are allowed.
     * 
     * @return the allowed actions
     */
    AuthorizeAction[] actions();
}
