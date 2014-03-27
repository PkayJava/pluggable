package com.itrustcambodia.pluggable.page;

import java.util.List;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractPlugin;
import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.ILoginPage;
import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.error.AbstractErrorPage;
import com.itrustcambodia.pluggable.layout.AbstractLayout;
import com.itrustcambodia.pluggable.layout.FullLayout;
import com.itrustcambodia.pluggable.layout.MenuLayout;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;

/**
 * @author Socheat KHAUV
 */
public abstract class WebPage extends org.apache.wicket.markup.html.WebPage {

    /**
     * 
     */
    private static final long serialVersionUID = 7456361864670996664L;

    public WebPage() {
        securityInterceptor();
    }

    public WebPage(IModel<?> model) {
        super(model);
        securityInterceptor();
    }

    public WebPage(PageParameters parameters) {
        super(parameters);
        securityInterceptor();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Label title = new Label("pageTitle", getPageTitle());
        add(title);
    }

    public abstract String getPageTitle();

    public abstract List<Menu> getPageMenus(Roles roles);

    public AbstractLayout requestLayout(String id) {
        Session session = getSession();
        Roles roles = null;
        if (session instanceof AbstractAuthenticatedWebSession) {
            roles = ((AbstractAuthenticatedWebSession) session).getRoles();
        } else {
            roles = new Roles();
        }
        List<Menu> menus = getPageMenus(roles);
        if (menus == null || menus.isEmpty()) {
            return new FullLayout(id);
        } else {
            return new MenuLayout(id);
        }
    }

    private void securityInterceptor() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();

        String identity = application.getPluginMapping(this.getClass()
                .getName());

        if (identity != null && !"".equals(identity)) {
            if (!(this instanceof PluginSettingPage)) {
                if (application.isMigrated()) {
                    AbstractPlugin plugin = application.getPlugin(identity);
                    if (!plugin.isActivated()) {
                        throw new RestartResponseException(
                                plugin.getSettingPage());
                    }
                } else {
                    throw new RestartResponseException(MigrationPage.class);
                }
            }
        } else {
            if (this instanceof ILoginPage) {
                Long count = jdbcTemplate.queryForObject(
                        "select count(*) from "
                                + TableUtilities.getTableName(application
                                        .getUserEntity()), Long.class);
                if (count <= 0) {
                    throw new RestartResponseException(InstallationPage.class);
                }
            } else if (this instanceof InstallationPage) {
                Long count = jdbcTemplate.queryForObject(
                        "select count(*) from "
                                + TableUtilities.getTableName(application
                                        .getUserEntity()), Long.class);
                if (count > 0) {
                    throw new RestartResponseException(getApplication()
                            .getHomePage());
                }
            } else if (this instanceof MigrationPage) {
                if (application.isMigrated()) {
                    throw new RestartResponseException(getApplication()
                            .getHomePage());
                }
            } else {
                if (!(this instanceof AbstractErrorPage)
                        && !(this instanceof LogoutPage)) {
                    Long count = jdbcTemplate.queryForObject(
                            "select count(*) from "
                                    + TableUtilities.getTableName(application
                                            .getUserEntity()), Long.class);
                    if (count <= 0) {
                        throw new RestartResponseException(
                                InstallationPage.class);
                    } else {
                        if (!application.isMigrated()) {
                            throw new RestartResponseException(
                                    MigrationPage.class);
                        }
                    }
                }
            }
        }
    }
}
