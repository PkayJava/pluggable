package com.angkorteam.pluggable.wicket.authroles.authorization.strategies.role;

import org.apache.wicket.authorization.strategies.CompoundAuthorizationStrategy;

import com.angkorteam.pluggable.wicket.authroles.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;
import com.angkorteam.pluggable.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;

/**
 * @author Socheat KHAUV
 */
public class RoleAuthorizationStrategy extends CompoundAuthorizationStrategy {
    /**
     * Construct.
     * 
     * @param roleCheckingStrategy
     *            the role checking strategy
     */
    public RoleAuthorizationStrategy(final IRoleCheckingStrategy roleCheckingStrategy) {
        add(new AnnotationsRoleAuthorizationStrategy(roleCheckingStrategy));
        add(new MetaDataRoleAuthorizationStrategy(roleCheckingStrategy));
    }
}
