package com.angkorteam.pluggable.framework.wicket.authroles.authentication;

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;

import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.Roles;

/**
 * @author Socheat KHAUV
 */
public abstract class AbstractAuthenticatedWebSession extends WebSession {
    private static final long serialVersionUID = 1L;

    /**
     * @return Current authenticated web session
     */
    public static AbstractAuthenticatedWebSession get() {
        return (AbstractAuthenticatedWebSession) Session.get();
    }

    /**
     * Construct.
     * 
     * @param request
     *            The current request object
     */
    public AbstractAuthenticatedWebSession(final Request request) {
        super(request);
    }

    /**
     * @return Get the roles that this session can play
     */
    public abstract Roles getRoles();

    /**
     * @return True if the user is signed in to this session
     */
    public abstract boolean isSignedIn();
}