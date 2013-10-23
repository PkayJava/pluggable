package com.itrustcambodia.pluggable.core;

import org.apache.wicket.request.Request;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.utilities.RoleUtilities;
import com.itrustcambodia.pluggable.utilities.SecurityUtilities;
import com.itrustcambodia.pluggable.wicket.authroles.authentication.AuthenticatedWebSession;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;

public class WebSession extends AuthenticatedWebSession {

    /**
     * 
     */
    private static final long serialVersionUID = 2305073826934854838L;

    private Roles roles = null;

    private String username;

    public WebSession(Request request) {
        super(request);
        // Injector.get().inject(this);
        this.roles = new Roles();
    }

    public final String getUsername() {
        return this.username;
    }

    @Override
    public boolean authenticate(String username, String password) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        boolean valid = SecurityUtilities.authenticate(jdbcTemplate, username, password);
        if (valid) {
            this.username = username;
            for (String role : RoleUtilities.lookupRoles(jdbcTemplate, username)) {
                roles.add(role);
            }
        }
        return valid;
    }

    @Override
    public Roles getRoles() {
        return this.roles;
    }

}
