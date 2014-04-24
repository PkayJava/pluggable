package com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.annotations;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.request.component.IRequestableComponent;

import com.angkorteam.pluggable.framework.wicket.authroles.Role;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.AbstractRoleAuthorizationStrategy;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.Roles;

/**
 * @author Socheat KHAUV
 */
public class AnnotationsRoleAuthorizationStrategy extends AbstractRoleAuthorizationStrategy {
    /**
     * Construct.
     * 
     * @param roleCheckingStrategy
     *            the authorizer delegate
     */
    public AnnotationsRoleAuthorizationStrategy(final IRoleCheckingStrategy roleCheckingStrategy) {
        super(roleCheckingStrategy);
    }

    /**
     * @see org.apache.wicket.authorization.IAuthorizationStrategy#isInstantiationAuthorized(java.lang.Class)
     */
    @Override
    public <T extends IRequestableComponent> boolean isInstantiationAuthorized(final Class<T> componentClass) {
        // We are authorized unless we are found not to be
        boolean authorized = true;

        // Check class annotation first because it is more specific than package
        // annotation
        final AuthorizeInstantiation classAnnotation = componentClass.getAnnotation(AuthorizeInstantiation.class);
        if (classAnnotation != null) {
            List<String> tmp = new ArrayList<String>();
            for (Role role : classAnnotation.roles()) {
                tmp.add(role.name());
            }
            authorized = hasAny(new Roles(tmp.toArray(new String[tmp.size()])));
        } else {
            // Check package annotation if there is no one on the the class
            final Package componentPackage = componentClass.getPackage();
            if (componentPackage != null) {
                final AuthorizeInstantiation packageAnnotation = componentPackage.getAnnotation(AuthorizeInstantiation.class);
                if (packageAnnotation != null) {
                    List<String> tmp = new ArrayList<String>();
                    for (Role role : packageAnnotation.roles()) {
                        tmp.add(role.name());
                    }
                    authorized = hasAny(new Roles(tmp.toArray(new String[tmp.size()])));
                }
            }
        }

        return authorized;
    }

    /**
     * @see org.apache.wicket.authorization.IAuthorizationStrategy#isActionAuthorized(org.apache.wicket.Component,
     *      org.apache.wicket.authorization.Action)
     */
    @Override
    public boolean isActionAuthorized(final Component component, final Action action) {
        // Get component's class
        final Class<?> componentClass = component.getClass();

        return isActionAuthorized(componentClass, action);
    }

    protected boolean isActionAuthorized(final Class<?> componentClass, final Action action) {
        // Check for a single action
        if (!check(action, componentClass.getAnnotation(AuthorizeAction.class))) {
            return false;
        }

        // Check for multiple actions
        final AuthorizeActions authorizeActionsAnnotation = componentClass.getAnnotation(AuthorizeActions.class);
        if (authorizeActionsAnnotation != null) {
            for (final AuthorizeAction authorizeActionAnnotation : authorizeActionsAnnotation.actions()) {
                if (!check(action, authorizeActionAnnotation)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * @param action
     *            The action to check
     * @param authorizeActionAnnotation
     *            The annotations information
     * @return False if the action is not authorized
     */
    private boolean check(final Action action, final AuthorizeAction authorizeActionAnnotation) {
        if (authorizeActionAnnotation != null) {
            if (action.getName().equals(authorizeActionAnnotation.action())) {
                List<String> roles = new ArrayList<String>();
                for (Role role : authorizeActionAnnotation.roles()) {
                    roles.add(role.name());
                }
                Roles acceptedRoles = new Roles(roles.toArray(new String[roles.size()]));
                if (!(isEmpty(acceptedRoles) || hasAny(acceptedRoles))) {
                    return false;
                }
            }
        }
        return true;
    }
}