package com.angkorteam.pluggable.framework.core;

import org.apache.wicket.request.Request;
import org.springframework.jdbc.core.JdbcTemplate;

import com.angkorteam.pluggable.framework.utilities.RoleUtilities;
import com.angkorteam.pluggable.framework.utilities.SecurityUtilities;
import com.angkorteam.pluggable.framework.wicket.authroles.authentication.AuthenticatedWebSession;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.Roles;

/**
 * @author Socheat KHAUV
 */
public class WebSession extends AuthenticatedWebSession {

    /**
     * 
     */
    private static final long serialVersionUID = 2305073826934854838L;

    private Roles roles = null;

    private String username;

    public WebSession(Request request) {
        super(request);
        this.roles = new Roles();
    }

    public final String getUsername() {
        return this.username;
    }

    @Override
    public boolean authenticate(String username, String password) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        boolean valid = false;
            JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
            valid = SecurityUtilities.authenticateJdbc(jdbcTemplate, username,
                    password);
            if (valid) {
                this.username = username;
                for (String role : RoleUtilities.lookupJdbcRoles(jdbcTemplate,
                        username)) {
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
