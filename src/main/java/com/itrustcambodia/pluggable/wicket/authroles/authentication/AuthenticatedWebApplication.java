package com.itrustcambodia.pluggable.wicket.authroles.authentication;

import java.lang.ref.WeakReference;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.Session;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;

import com.itrustcambodia.pluggable.page.WebPage;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.RoleAuthorizationStrategy;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;

/**
 * @author Socheat KHAUV
 */
public abstract class AuthenticatedWebApplication extends WebApplication implements IRoleCheckingStrategy, IUnauthorizedComponentInstantiationListener {
    /** Subclass of authenticated web session to instantiate */
    private final WeakReference<Class<? extends AbstractAuthenticatedWebSession>> webSessionClassRef;

    /**
     * Constructor
     */
    public AuthenticatedWebApplication() {
        // Get web session class to instantiate
        webSessionClassRef = new WeakReference<Class<? extends AbstractAuthenticatedWebSession>>(getWebSessionClass());
    }

    /**
     * @see org.apache.wicket.protocol.http.WebApplication#init()
     */
    @Override
    protected void init() {
        super.init();

        // Set authorization strategy and unauthorized instantiation listener
        getSecuritySettings().setAuthorizationStrategy(new RoleAuthorizationStrategy(this));
        getSecuritySettings().setUnauthorizedComponentInstantiationListener(this);
    }

    /**
     * @see IRoleCheckingStrategy#hasAnyRole(Roles)
     */
    @Override
    public final boolean hasAnyRole(final Roles roles) {
        final Roles sessionRoles = AbstractAuthenticatedWebSession.get().getRoles();
        return (sessionRoles != null) && sessionRoles.hasAnyRole(roles);
    }

    /**
     * @see IUnauthorizedComponentInstantiationListener#onUnauthorizedInstantiation(Component)
     */
    @Override
    public final void onUnauthorizedInstantiation(final Component component) {
        // If there is a sign in page class declared, and the unauthorized
        // component is a page, but it's not the sign in page
        if (component instanceof Page) {
            if (!AbstractAuthenticatedWebSession.get().isSignedIn()) {
                // Redirect to intercept page to let the user sign in
                restartResponseAtSignInPage();
            } else {
                onUnauthorizedPage((Page) component);
            }
        } else {
            // The component was not a page, so throw an exception
            throw new UnauthorizedInstantiationException(component.getClass());
        }
    }

    /**
     * Restarts response at sign in page.
     * 
     * NOTE: this method internally throws a restart response exception, so no
     * code after a call to this method will be executed
     */
    public void restartResponseAtSignInPage() {
        throw new RestartResponseAtInterceptPageException(getSignInPageClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session newSession(final Request request, final Response response) {
        try {
            return webSessionClassRef.get().getDeclaredConstructor(Request.class).newInstance(request);
        } catch (Exception e) {
            throw new WicketRuntimeException("Unable to instantiate web session " + webSessionClassRef.get(), e);
        }
    }

    /**
     * @return BaseAuthenticatedWebSession subclass to use in this authenticated
     *         web application.
     */
    protected abstract Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass();

    /**
     * @return Subclass of sign-in page
     */
    protected abstract Class<? extends WebPage> getSignInPageClass();

    /**
     * Called when an AUTHENTICATED user tries to navigate to a page that they
     * are not authorized to access. You might want to override this to navigate
     * to some explanatory page or to the application's home page.
     * 
     * @param page
     *            The page
     */
    protected void onUnauthorizedPage(final Page page) {
        // The component was not a page, so throw an exception
        throw new UnauthorizedInstantiationException(page.getClass());
    }
}