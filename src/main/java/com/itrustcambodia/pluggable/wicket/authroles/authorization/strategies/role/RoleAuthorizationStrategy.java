package com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role;

import org.apache.wicket.authorization.strategies.CompoundAuthorizationStrategy;

import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;

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
