package com.itrustcambodia.pluggable.utilities;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.core.request.handler.IPageRequestHandler;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.settings.IExceptionSettings;
import org.apache.wicket.settings.IExceptionSettings.AjaxErrorStrategy;
import org.apache.wicket.settings.IExceptionSettings.ThreadDumpStrategy;
import org.reflections.Reflections;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.itrustcambodia.pluggable.PluggableConstants;
import com.itrustcambodia.pluggable.core.AbstractPlugin;
import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.database.Schema;
import com.itrustcambodia.pluggable.database.Table;
import com.itrustcambodia.pluggable.entity.AbstractUser;
import com.itrustcambodia.pluggable.entity.ApplicationRegistry;
import com.itrustcambodia.pluggable.entity.ApplicationSetting;
import com.itrustcambodia.pluggable.entity.Group;
import com.itrustcambodia.pluggable.entity.PluginRegistry;
import com.itrustcambodia.pluggable.entity.PluginSetting;
import com.itrustcambodia.pluggable.entity.Role;
import com.itrustcambodia.pluggable.entity.RoleGroup;
import com.itrustcambodia.pluggable.entity.RoleUser;
import com.itrustcambodia.pluggable.entity.UserGroup;
import com.itrustcambodia.pluggable.error.AccessDeniedPage;
import com.itrustcambodia.pluggable.error.ExceptionErrorPage;
import com.itrustcambodia.pluggable.error.InternalErrorPage;
import com.itrustcambodia.pluggable.error.PageExpiredErrorPage;
import com.itrustcambodia.pluggable.migration.AbstractPluginMigrator;
import com.itrustcambodia.pluggable.page.GroupManagementPage;
import com.itrustcambodia.pluggable.page.JsonDocPage;
import com.itrustcambodia.pluggable.page.JvmPage;
import com.itrustcambodia.pluggable.page.RoleManagementPage;
import com.itrustcambodia.pluggable.page.UserManagementPage;
import com.itrustcambodia.pluggable.page.WebPage;
import com.itrustcambodia.pluggable.rest.Controller;
import com.itrustcambodia.pluggable.wicket.authroles.Secured;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeActions;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 * @author Socheat KHAUV
 */
public class FrameworkUtilities {

    private FrameworkUtilities() {
    }

    public static boolean hasAccess(Roles roles, Class<?> clazz) {
        AuthorizeInstantiation authorizeInstantiation = clazz.getAnnotation(AuthorizeInstantiation.class);
        if (authorizeInstantiation == null || authorizeInstantiation.roles() == null || authorizeInstantiation.roles().length == 0) {
            return true;
        } else {
            for (com.itrustcambodia.pluggable.wicket.authroles.Role role : authorizeInstantiation.roles()) {
                if (roles.hasRole(role.name())) {
                    return true;
                }
            }
            return false;
        }
    }

    public static final void initExceptionPages(WebApplication application) {
        application.getExceptionSettings().setUnexpectedExceptionDisplay(IExceptionSettings.SHOW_EXCEPTION_PAGE);
        application.getExceptionSettings().setThreadDumpStrategy(ThreadDumpStrategy.ALL_THREADS);
        application.getExceptionSettings().setAjaxErrorHandlingStrategy(AjaxErrorStrategy.REDIRECT_TO_ERROR_PAGE);

        application.getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
        application.getApplicationSettings().setInternalErrorPage(InternalErrorPage.class);
        application.getApplicationSettings().setPageExpiredErrorPage(PageExpiredErrorPage.class);

        application.getRequestCycleListeners().add(new AbstractRequestCycleListener() {
            @Override
            public IRequestHandler onException(RequestCycle cycle, Exception e) {

                Page currentPage = null;

                IRequestHandler handler = cycle.getActiveRequestHandler();

                if (handler == null) {
                    handler = cycle.getRequestHandlerScheduledAfterCurrent();
                }

                if (handler instanceof IPageRequestHandler) {
                    IPageRequestHandler pageRequestHandler = (IPageRequestHandler) handler;
                    currentPage = (Page) pageRequestHandler.getPage();
                }

                return new RenderPageRequestHandler(new PageProvider(new ExceptionErrorPage(e, currentPage)));
            }
        });

    }

    public static final Map<String, String> lookupRoles(String... javaPackages) {
        Map<String, String> roles = new HashMap<String, String>();
        for (String javaPackage : javaPackages) {
            Reflections reflections = new Reflections(javaPackage);
            Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(AuthorizeAction.class);
            if (annotated != null && !annotated.isEmpty()) {
                for (Class<?> clazz : annotated) {
                    AuthorizeAction authorizeAction = clazz.getAnnotation(AuthorizeAction.class);
                    if (authorizeAction.roles() != null && authorizeAction.roles().length > 0) {
                        for (com.itrustcambodia.pluggable.wicket.authroles.Role role : authorizeAction.roles()) {
                            if (!roles.containsKey(role.name())) {
                                roles.put(role.name(), role.description());
                            }
                        }
                    }
                }
            }

            annotated = reflections.getTypesAnnotatedWith(AuthorizeActions.class);
            if (annotated != null && !annotated.isEmpty()) {
                for (Class<?> clazz : annotated) {
                    AuthorizeActions authorizeActions = clazz.getAnnotation(AuthorizeActions.class);
                    if (authorizeActions.actions() != null && authorizeActions.actions().length > 0) {
                        for (AuthorizeAction authorizeAction : authorizeActions.actions()) {
                            if (authorizeAction.roles() != null && authorizeAction.roles().length > 0) {
                                for (com.itrustcambodia.pluggable.wicket.authroles.Role role : authorizeAction.roles()) {
                                    if (!roles.containsKey(role.name())) {
                                        roles.put(role.name(), role.description());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            annotated = reflections.getTypesAnnotatedWith(AuthorizeInstantiation.class);
            if (annotated != null && !annotated.isEmpty()) {
                for (Class<?> clazz : annotated) {
                    AuthorizeInstantiation authorizeInstantiation = clazz.getAnnotation(AuthorizeInstantiation.class);
                    if (authorizeInstantiation.roles() != null && authorizeInstantiation.roles().length > 0) {
                        for (com.itrustcambodia.pluggable.wicket.authroles.Role role : authorizeInstantiation.roles()) {
                            if (!roles.containsKey(role.name())) {
                                roles.put(role.name(), role.description());
                            }
                        }
                    }
                }
            }

            Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
            for (Class<?> controller : controllers) {
                for (Method method : org.reflections.ReflectionUtils.getAllMethods(controller)) {
                    Secured secured = method.getAnnotation(Secured.class);
                    if (secured != null && secured.roles() != null && secured.roles().length > 0) {
                        for (com.itrustcambodia.pluggable.wicket.authroles.Role role : secured.roles()) {
                            if (!roles.containsKey(role.name())) {
                                roles.put(role.name(), role.description());
                            }
                        }
                    }
                }
            }
        }
        return roles;
    }

    public static String getClientIP(HttpServletRequest request) {
        if (StringUtils.isNotBlank(request.getHeader(PluggableConstants.X_FORWARDED_FOR))) {
            try {
                return StringUtils.split(request.getHeader(PluggableConstants.X_FORWARDED_FOR), ",")[0];
            } catch (IndexOutOfBoundsException e) {
                return request.getRemoteAddr();
            }
        } else {
            return request.getRemoteAddr();
        }
    }

    public static final Roles lookupRoles(Class<? extends WebPage> clazz) {
        Roles roles = new Roles();

        AuthorizeAction authorizeAction = clazz.getAnnotation(AuthorizeAction.class);

        if (authorizeAction != null && authorizeAction.roles() != null && authorizeAction.roles().length > 0) {
            for (com.itrustcambodia.pluggable.wicket.authroles.Role role : authorizeAction.roles()) {
                if (!roles.contains(role.name())) {
                    roles.add(role.name());
                }
            }
        }

        AuthorizeActions authorizeActions = clazz.getAnnotation(AuthorizeActions.class);
        if (authorizeActions != null && authorizeActions.actions() != null && authorizeActions.actions().length > 0) {
            for (AuthorizeAction action : authorizeActions.actions()) {
                if (action.roles() != null && action.roles().length > 0) {
                    for (com.itrustcambodia.pluggable.wicket.authroles.Role role : action.roles()) {
                        if (!roles.contains(role.name())) {
                            roles.add(role.name());
                        }
                    }
                }
            }
        }

        AuthorizeInstantiation authorizeInstantiation = clazz.getAnnotation(AuthorizeInstantiation.class);
        if (authorizeInstantiation != null && authorizeInstantiation.roles() != null && authorizeInstantiation.roles().length > 0) {
            for (com.itrustcambodia.pluggable.wicket.authroles.Role role : authorizeInstantiation.roles()) {
                if (!roles.contains(role.name())) {
                    roles.add(role.name());
                }
            }
        }

        return roles;
    }

    public static final Menu getSecurityMenu(AbstractWebApplication application, Roles roles) {
        List<Menu> children = new ArrayList<Menu>();
        if (roles.hasAnyRole(FrameworkUtilities.lookupRoles(GroupManagementPage.class))) {
            children.add(Menu.linkMenu(AbstractWebApplication.GROUP_LAEBL, GroupManagementPage.class));
        }
        if (roles.hasAnyRole(FrameworkUtilities.lookupRoles(UserManagementPage.class))) {
            children.add(Menu.linkMenu(AbstractWebApplication.USER_LABEL, UserManagementPage.class));
        }
        if (roles.hasAnyRole(FrameworkUtilities.lookupRoles(RoleManagementPage.class))) {
            children.add(Menu.linkMenu(AbstractWebApplication.ROLE_LABEL, RoleManagementPage.class));
        }
        if (roles.hasAnyRole(FrameworkUtilities.lookupRoles(JvmPage.class))) {
            children.add(Menu.linkMenu(AbstractWebApplication.JVM_LABEL, JvmPage.class));
        }
        if (application.getSettingPage() != null) {
            if (roles.hasAnyRole(FrameworkUtilities.lookupRoles(application.getSettingPage()))) {
                children.add(Menu.dividerMenu());
                children.add(Menu.linkMenu(AbstractWebApplication.SETTING_LABEL, application.getSettingPage()));
            }
        }
        if (roles.hasAnyRole(FrameworkUtilities.lookupRoles(JsonDocPage.class))) {
            children.add(Menu.dividerMenu());
            children.add(Menu.linkMenu(AbstractWebApplication.REST_API_LABEL, JsonDocPage.class));
        }
        if (!children.isEmpty()) {
            if (children.get(0).getType() == Menu.Type.DIVIDER) {
                children.remove(0);
            }
        }
        Menu security = Menu.parentMenu(AbstractWebApplication.SECURITY_LABEL, children);
        return security;
    }

    public static final boolean isPluginActivated(JdbcTemplate jdbcTemplate, String identity) {
        try {
            PluginRegistry pluginRegistry = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(PluginRegistry.class) + " where " + PluginRegistry.IDENTITY + " = ?", new EntityRowMapper<PluginRegistry>(PluginRegistry.class), identity);
            return pluginRegistry.isActivated();
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public static final String getServerAddress(HttpServletRequest request) {
        StringBuffer address = new StringBuffer();
        if (request.isSecure()) {
            address.append("https://" + request.getServerName());
            if (request.getServerPort() != 443) {
                address.append(":" + request.getServerPort());
            }
        } else {
            address.append("http://" + request.getServerName());
            if (request.getServerPort() != 80) {
                address.append(":" + request.getServerPort());
            }
        }
        if (request.getContextPath() != null && !"".equals(request.getContextPath())) {
            address.append(request.getContextPath());
        }
        return address.toString();
    }

    public static final void initSecurityTable(AbstractWebApplication application, Schema schema, JdbcTemplate jdbcTemplate, Map<String, AbstractPlugin> plugins) {
        Table table = null;

        table = schema.getTable(Role.class);
        if (!table.exists()) {
            schema.createTable(Role.class, Role.ID, Role.NAME, Role.DESCRIPTION, Role.DISABLE);
        }

        table = schema.getTable(application.getUserEntity());
        if (!table.exists()) {
            schema.createTable(application.getUserEntity(), AbstractUser.ID, AbstractUser.LOGIN, AbstractUser.PASSWORD, AbstractUser.DISABLE);
        }

        table = schema.getTable(Group.class);
        if (!table.exists()) {
            schema.createTable(Group.class, Group.ID, Group.NAME, Group.DESCRIPTION, Group.DISABLE);
        }

        table = schema.getTable(UserGroup.class);
        if (!table.exists()) {
            schema.createTable(UserGroup.class, UserGroup.GROUP_ID, UserGroup.USER_ID);
        }

        table = schema.getTable(RoleUser.class);
        if (!table.exists()) {
            schema.createTable(RoleUser.class, RoleUser.USER_ID, RoleUser.ROLE_ID);
        }

        table = schema.getTable(RoleGroup.class);
        if (!table.exists()) {
            schema.createTable(RoleGroup.class, RoleGroup.ROLE_ID, RoleGroup.GROUP_ID);
        }
    }

    public static final void initRegistryTable(AbstractWebApplication application, Schema schema, JdbcTemplate jdbcTemplate, Map<String, AbstractPlugin> plugins) {
        Table table = null;

        table = schema.getTable(ApplicationRegistry.class);
        if (!table.exists()) {
            schema.createTable(ApplicationRegistry.class, ApplicationRegistry.ID, ApplicationRegistry.VERSION, ApplicationRegistry.UPGRADE_DATE);
        }

        table = schema.getTable(PluginRegistry.class);
        if (!table.exists()) {
            schema.createTable(PluginRegistry.class, PluginRegistry.ID, PluginRegistry.NAME, PluginRegistry.VERSION, PluginRegistry.IDENTITY, PluginRegistry.ACTIVATED, PluginRegistry.PRESENTED, PluginRegistry.UPGRADE_DATE);
        }

        table = schema.getTable(PluginSetting.class);
        if (!table.exists()) {
            schema.createTable(PluginSetting.class, PluginSetting.ID, PluginSetting.IDENTITY, PluginSetting.NAME, PluginSetting.VALUE);
        }

        table = schema.getTable(ApplicationSetting.class);
        if (!table.exists()) {
            schema.createTable(ApplicationSetting.class, ApplicationSetting.ID, ApplicationSetting.NAME, ApplicationSetting.VALUE);
        }

        jdbcTemplate.update("UPDATE " + TableUtilities.getTableName(PluginRegistry.class) + " set " + PluginRegistry.PRESENTED + " = ?", false);
        if (plugins != null && !plugins.isEmpty()) {
            for (Entry<String, AbstractPlugin> entry : plugins.entrySet()) {
                AbstractPlugin plugin = entry.getValue();

                PluginRegistry pluginRegistry = null;
                try {
                    pluginRegistry = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(PluginRegistry.class) + " where " + PluginRegistry.IDENTITY + " = ?", new EntityRowMapper<PluginRegistry>(PluginRegistry.class), plugin.getIdentity());
                } catch (EmptyResultDataAccessException e) {
                }
                if (pluginRegistry != null) {
                    boolean activated = pluginRegistry.isActivated();
                    AbstractPluginMigrator migrator = (AbstractPluginMigrator) application.getBean(plugin.getMigrator().getName());
                    if (migrator.getVersion() > pluginRegistry.getVersion()) {
                        activated = false;
                    }
                    jdbcTemplate.update("update " + TableUtilities.getTableName(PluginRegistry.class) + " set " + PluginRegistry.PRESENTED + " = ?, " + PluginRegistry.ACTIVATED + " = ? where " + PluginRegistry.IDENTITY + " = ?", true, activated, plugin.getIdentity());
                } else {
                    SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
                    insert.withTableName(TableUtilities.getTableName(PluginRegistry.class));
                    Map<String, Object> fields = new HashMap<String, Object>();
                    fields.put(PluginRegistry.IDENTITY, plugin.getIdentity());
                    fields.put(PluginRegistry.NAME, plugin.getName());
                    fields.put(PluginRegistry.ACTIVATED, false);
                    fields.put(PluginRegistry.PRESENTED, true);
                    fields.put(PluginRegistry.VERSION, 0d);
                    insert.execute(fields);
                }
            }
        }

        double version = 0.00d;
        ApplicationRegistry applicationRegistry = null;
        try {
            applicationRegistry = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(ApplicationRegistry.class) + " order by " + ApplicationRegistry.VERSION + " desc limit 1", new EntityRowMapper<ApplicationRegistry>(ApplicationRegistry.class));
        } catch (EmptyResultDataAccessException e) {
        }
        if (applicationRegistry == null) {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
            insert.withTableName(TableUtilities.getTableName(ApplicationRegistry.class));
            Map<String, Object> fields = new HashMap<String, Object>();
            fields.put(ApplicationRegistry.VERSION, version);
            fields.put(ApplicationRegistry.UPGRADE_DATE, new Date());
            insert.execute(fields);
        }
    }

    public static final List<Menu> getPluginMenus(JdbcTemplate jdbcTemplate, Map<String, AbstractPlugin> plugins) {
        List<Menu> pluginMenus = new ArrayList<Menu>();
        if (plugins != null && !plugins.isEmpty()) {
            for (Entry<String, AbstractPlugin> entry : plugins.entrySet()) {
                AbstractPlugin plugin = entry.getValue();
                PluginRegistry pluginRegistry = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(PluginRegistry.class) + " where " + PluginRegistry.IDENTITY + " = ?", new EntityRowMapper<PluginRegistry>(PluginRegistry.class), entry.getKey());
                if (pluginRegistry.isActivated()) {
                    Menu menu = Menu.linkMenu(plugin.getName(), plugin.getDashboardPage());
                    pluginMenus.add(menu);
                } else {
                    Menu menu = Menu.linkMenu(plugin.getName(), plugin.getSettingPage());
                    pluginMenus.add(menu);
                }
            }
        }
        return pluginMenus;
    }

}
