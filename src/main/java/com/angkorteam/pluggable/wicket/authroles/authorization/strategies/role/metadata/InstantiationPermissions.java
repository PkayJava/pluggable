package com.angkorteam.pluggable.wicket.authroles.authorization.strategies.role.metadata;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.request.component.IRequestableComponent;
import org.apache.wicket.util.io.IClusterable;

import com.angkorteam.pluggable.wicket.authroles.authorization.strategies.role.Roles;

/**
 * @author Socheat KHAUV
 */
public class InstantiationPermissions implements IClusterable {
    private static final long serialVersionUID = 1L;

    /** Holds roles objects for component classes */
    private final Map<Class<? extends Component>, Roles> rolesForComponentClass = new HashMap<Class<? extends Component>, Roles>();

    /**
     * Gives the given role permission to instantiate the given class.
     * 
     * @param <T>
     * @param componentClass
     *            The component class
     * @param rolesToAdd
     *            The roles to add
     */
    public final <T extends Component> void authorize(final Class<T> componentClass, final Roles rolesToAdd) {
        if (componentClass == null) {
            throw new IllegalArgumentException("Argument componentClass cannot be null");
        }

        if (rolesToAdd == null) {
            throw new IllegalArgumentException("Argument rolesToadd cannot be null");
        }

        Roles roles = rolesForComponentClass.get(componentClass);
        if (roles == null) {
            roles = new Roles();
            rolesForComponentClass.put(componentClass, roles);
        }
        roles.addAll(rolesToAdd);
    }

    /**
     * Gives all roles permission to instantiate the given class. Note that this
     * is only relevant if a role was previously authorized for that class. If
     * no roles where previously authorized the effect of the unauthorize call
     * is that no roles at all will be authorized for that class.
     * 
     * @param <T>
     * @param componentClass
     *            The component class
     */
    public final <T extends Component> void authorizeAll(final Class<T> componentClass) {
        if (componentClass == null) {
            throw new IllegalArgumentException("Argument componentClass cannot be null");
        }

        rolesForComponentClass.remove(componentClass);
    }

    /**
     * Gets the roles that have a binding with the given component class.
     * 
     * @param <T>
     * 
     * @param componentClass
     *            the component class
     * @return the roles that have a binding with the given component class, or
     *         null if no entries are found
     */
    public <T extends IRequestableComponent> Roles authorizedRoles(final Class<T> componentClass) {
        if (componentClass == null) {
            throw new IllegalArgumentException("Argument componentClass cannot be null");
        }

        return rolesForComponentClass.get(componentClass);
    }

    /**
     * Removes permission for the given role to instantiate the given class.
     * 
     * @param <T>
     * 
     * @param componentClass
     *            The class
     * @param rolesToRemove
     *            The role to deny
     */
    public final <T extends Component> void unauthorize(final Class<T> componentClass, final Roles rolesToRemove) {
        if (componentClass == null) {
            throw new IllegalArgumentException("Argument componentClass cannot be null");
        }

        if (rolesToRemove == null) {
            throw new IllegalArgumentException("Argument rolesToRemove cannot be null");
        }

        Roles roles = rolesForComponentClass.get(componentClass);
        if (roles != null) {
            roles.removeAll(rolesToRemove);
        } else {
            roles = new Roles();
            rolesForComponentClass.put(componentClass, roles);
        }

        // If we removed the last authorized role, we authorize the empty role
        // so that removing authorization can't suddenly open something up to
        // everyone.
        if (roles.size() == 0) {
            roles.add(MetaDataRoleAuthorizationStrategy.NO_ROLE);
        }
    }

    /**
     * @return gets map with roles objects for a component classes
     */
    protected final Map<Class<? extends Component>, Roles> getRolesForComponentClass() {
        return rolesForComponentClass;
    }
}