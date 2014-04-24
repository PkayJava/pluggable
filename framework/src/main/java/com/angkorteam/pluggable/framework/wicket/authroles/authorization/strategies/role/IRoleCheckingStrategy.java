package com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role;

/**
 * @author Socheat KHAUV
 */

public interface IRoleCheckingStrategy {
    /**
     * Whether any of the given roles matches. For example, if a user has role
     * USER and the provided roles are {USER, ADMIN} this method should return
     * true as the user has at least one of the roles that were provided.
     * 
     * @param roles
     *            the roles
     * @return true if a user or whatever subject this implementation wants to
     *         work with has at least on of the provided roles
     */
    boolean hasAnyRole(Roles roles);
}