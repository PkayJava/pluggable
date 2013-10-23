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
public @interface AuthorizeInstantiation {

    /**
     * Gets the roles that are allowed to take the action.
     * 
     * @return the roles that are allowed. Returns a zero length array by
     *         default
     */
    Role[] roles() default {};
}