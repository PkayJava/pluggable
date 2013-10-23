package com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.itrustcambodia.pluggable.wicket.authroles.Role;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.TYPE })
@Documented
@Inherited
public @interface AuthorizeAction {

    /**
     * The action that is allowed. The default actions that are supported by
     * Wicket are <code>RENDER</code> and
     * <code>ENABLE<code> as defined as constants
     * of {@link org.apache.wicket.Component}.
     * 
     * @see org.apache.wicket.Component#RENDER
     * @see org.apache.wicket.Component#ENABLE
     * 
     * @return the action that is allowed
     */
    String action();

    /**
     * The roles for this action.
     * 
     * @return the roles for this action. The default is an empty string
     *         (annotations do not allow null default values)
     */
    Role[] roles() default {};

}