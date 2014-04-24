package com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role;

import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.util.lang.Args;

/**
 * @author Socheat KHAUV
 */
public abstract class AbstractRoleAuthorizationStrategy implements IAuthorizationStrategy {
    /** Role checking strategy. */
    private final IRoleCheckingStrategy roleCheckingStrategy;

    /**
     * Construct.
     * 
     * @param roleCheckingStrategy
     *            the authorizer delegate
     */
    public AbstractRoleAuthorizationStrategy(IRoleCheckingStrategy roleCheckingStrategy) {
        Args.notNull(roleCheckingStrategy, "roleCheckingStrategy");
        this.roleCheckingStrategy = roleCheckingStrategy;
    }

    /**
     * Gets whether any of the given roles applies to the authorizer.
     * 
     * @param roles
     *            the roles
     * @return whether any of the given roles applies to the authorizer
     */
    protected final boolean hasAny(Roles roles) {
        if (roles.isEmpty()) {
            return true;
        } else {
            return roleCheckingStrategy.hasAnyRole(roles);
        }
    }

    /**
     * Conducts a check to see if the roles object is empty. Since the roles
     * object does not contain any null values and will always hold an empty
     * string, an extra test is required beyond roles.isEmpty().
     * 
     * @param roles
     *            the Roles object to test
     * @return true if the object holds no real roles
     */
    protected final boolean isEmpty(Roles roles) {
        if (roles.isEmpty()) {
            return true;
        }

        return false;
    }
}
