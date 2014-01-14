package com.itrustcambodia.pluggable.core;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.settings.IExceptionSettings;
import org.quartz.SchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itrustcambodia.pluggable.database.DbSupport;
import com.itrustcambodia.pluggable.database.DbSupportFactory;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.database.Schema;
import com.itrustcambodia.pluggable.entity.AbstractUser;
import com.itrustcambodia.pluggable.entity.ApplicationRegistry;
import com.itrustcambodia.pluggable.entity.Group;
import com.itrustcambodia.pluggable.entity.Role;
import com.itrustcambodia.pluggable.migration.AbstractApplicationMigrator;
import com.itrustcambodia.pluggable.migration.AbstractPluginMigrator;
import com.itrustcambodia.pluggable.migration.ApplicationMigrator;
import com.itrustcambodia.pluggable.page.ApplicationSettingPage;
import com.itrustcambodia.pluggable.page.DashboardPage;
import com.itrustcambodia.pluggable.page.EditGroupPage;
import com.itrustcambodia.pluggable.page.EditUserPage;
import com.itrustcambodia.pluggable.page.GroupManagementPage;
import com.itrustcambodia.pluggable.page.HomePage;
import com.itrustcambodia.pluggable.page.LoginPage;
import com.itrustcambodia.pluggable.page.NewGroupPage;
import com.itrustcambodia.pluggable.page.NewUserPage;
import com.itrustcambodia.pluggable.page.SettingPage;
import com.itrustcambodia.pluggable.page.UserManagementPage;
import com.itrustcambodia.pluggable.page.WebPage;
import com.itrustcambodia.pluggable.quartz.Job;
import com.itrustcambodia.pluggable.quartz.Scheduled;
import com.itrustcambodia.pluggable.utilities.FrameworkUtilities;
import com.itrustcambodia.pluggable.utilities.GroupUtilities;
import com.itrustcambodia.pluggable.utilities.JobUtilities;
import com.itrustcambodia.pluggable.utilities.RegistryUtilities;
import com.itrustcambodia.pluggable.utilities.RoleUtilities;
import com.itrustcambodia.pluggable.utilities.SecurityUtilities;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.wicket.RequestMappingInfo;
import com.itrustcambodia.pluggable.wicket.authroles.authentication.AuthenticatedWebApplication;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;

/**
 * @author Socheat KHAUV
 */
public abstract class AbstractWebApplication extends
        AuthenticatedWebApplication implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6922345480297586360L;

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractWebApplication.class);

    public static final String SUPER_ADMIN_GROUP = "Super Admin Group";

    public static final String SECURITY_LABEL = "Security";

    public static final String SETTING_LABEL = "Setting";

    public static final String REST_API_LABEL = "Rest API";

    public static final String BEE_API_LABEL = "Bee API";

    public static final String USER_LABEL = "User";

    public static final String JOB_LABEL = "Job";

    public static final String ROLE_LABEL = "Role";

    public static final String GROUP_LAEBL = "Group";

    public static final String JVM_LABEL = "JVM";

    private Map<String, String> getPluginMapping() {
        Map<String, String> pluginMapping = (Map<String, String>) getServletContext()
                .getAttribute("PluginMapping");
        if (pluginMapping == null) {
            pluginMapping = new HashMap<String, String>();
            getServletContext().setAttribute("PluginMapping", pluginMapping);
        }
        return pluginMapping;
    }

    private ScheduledTaskRegistrar getScheduledTaskRegistrar() {
        ScheduledTaskRegistrar registrar = (ScheduledTaskRegistrar) getServletContext()
                .getAttribute("ScheduledTaskRegistrar");
        if (registrar == null) {
            registrar = new ScheduledTaskRegistrar();
            ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
            executor.setDaemon(true);
            executor.setPoolSize(10);
            executor.afterPropertiesSet();
            registrar.setTaskScheduler(executor);
            getServletContext().setAttribute("ScheduledTaskRegistrar",
                    registrar);
        }
        return registrar;
    }

    private Map<String, Class<? extends WebPage>> getMounts() {
        Map<String, Class<? extends WebPage>> mounts = (Map<String, Class<? extends WebPage>>) getServletContext()
                .getAttribute("Mounts");
        if (mounts == null) {
            mounts = new HashMap<String, Class<? extends WebPage>>();
            getServletContext().setAttribute("Mounts", mounts);
        }
        return mounts;
    }

    public Map<String, RequestMappingInfo> getControllers() {
        Map<String, RequestMappingInfo> controllers = (Map<String, RequestMappingInfo>) getServletContext()
                .getAttribute("Controllers");
        if (controllers == null) {
            controllers = new HashMap<String, RequestMappingInfo>();
            getServletContext().setAttribute("Controllers", controllers);
        }
        return controllers;
    }

    public Map<String, Object> getBeans() {
        Map<String, Object> beans = (Map<String, Object>) getServletContext()
                .getAttribute("Beans");
        if (beans == null) {
            beans = new HashMap<String, Object>();
            getServletContext().setAttribute("Beans", beans);
        }
        return beans;
    }

    private Map<String, String> getRoles() {
        Map<String, String> roles = (Map<String, String>) getServletContext()
                .getAttribute("Roles");
        if (roles == null) {
            roles = new HashMap<String, String>();
            getServletContext().setAttribute("Roles", roles);
        }
        return roles;
    }

    private Map<String, List<String>> getGroups() {
        Map<String, List<String>> groups = (Map<String, List<String>>) getServletContext()
                .getAttribute("Groups");
        if (groups == null) {
            groups = new HashMap<String, List<String>>();
            getServletContext().setAttribute("Groups", groups);
        }
        return groups;
    }

    private List<Class<? extends Job>> getJobs() {
        List<Class<? extends Job>> jobs = (List<Class<? extends Job>>) getServletContext()
                .getAttribute("Jobs");
        if (jobs == null) {
            jobs = new ArrayList<Class<? extends Job>>();
            getServletContext().setAttribute("Jobs", jobs);
        }
        return jobs;
    }

    private int nullType;

    @Override
    protected Class<? extends WebSession> getWebSessionClass() {
        return WebSession.class;
    }

    @Override
    public Class<? extends WebPage> getSignInPageClass() {
        return LoginPage.class;
    }

    protected abstract DataSource getDataSource();

    @Override
    protected void init() {
        super.init();
        LOGGER.info("starting application framework");

        DataSource dataSource = getDataSource();
        addBean(DataSource.class, dataSource);

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        addBean(JdbcTemplate.class, jdbcTemplate);

        DbSupport support = DbSupportFactory.createDbSupport(jdbcTemplate);
        addBean(DbSupport.class, support);

        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting()
                .create();
        addBean(Gson.class, gson);

        Schema schema = support.getSchema();
        addBean(Schema.class, schema);

        try {
            AbstractApplicationMigrator migrator = getMigrator().newInstance();
            migrator.setApplication(this);
            addBean(getMigrator().getName(), migrator);
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }

        Map<String, AbstractPlugin> plugins = getPlugins();
        for (AbstractPlugin plugin : plugins.values()) {
            try {
                AbstractPluginMigrator migrator = plugin.getMigrator()
                        .newInstance();
                migrator.setApplication(this);
                addBean(plugin.getMigrator().getName(), migrator);
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            }
        }

        FrameworkUtilities.initSecurityTable(this, schema, jdbcTemplate,
                plugins);

        FrameworkUtilities.initRegistryTable(this, schema, jdbcTemplate,
                plugins);

        List<String> pooledRoles = jdbcTemplate
                .queryForList(
                        "select " + Role.NAME + " from "
                                + TableUtilities.getTableName(Role.class),
                        String.class);

        Map<String, Role> mapping = new HashMap<String, Role>();

        Group adminGroup = GroupUtilities.createGroup(jdbcTemplate,
                AbstractWebApplication.SUPER_ADMIN_GROUP,
                AbstractWebApplication.SUPER_ADMIN_GROUP, false);
        if (getRoles() != null && !getRoles().isEmpty()) {
            for (Entry<String, String> role : getRoles().entrySet()) {
                pooledRoles.remove(role.getKey());
                mapping.put(role.getKey(), RoleUtilities.createRole(
                        getBean(DataSource.class), role.getKey(),
                        role.getValue(), false));
                SecurityUtilities.grantAccess(jdbcTemplate, adminGroup,
                        mapping.get(role.getKey()));
            }
        }

        FrameworkUtilities.initExceptionPages(this);

        if (!pooledRoles.isEmpty()) {
            for (String pooledRole : pooledRoles) {
                RoleUtilities.removeRole(jdbcTemplate, pooledRole);
            }
            pooledRoles.clear();
        }

        for (Entry<String, List<String>> roles : getGroups().entrySet()) {
            Group group = GroupUtilities.createGroup(jdbcTemplate,
                    roles.getKey(), getPlugin(roles.getKey()).getName(), false);
            for (String role : roles.getValue()) {
                SecurityUtilities.grantAccess(jdbcTemplate, group,
                        mapping.get(role));
            }
        }

        for (Entry<String, Class<? extends WebPage>> page : getMounts()
                .entrySet()) {
            LOGGER.info("mounted {} to {}", page.getKey(), page.getValue()
                    .getName());
            mountPage(page.getKey(), page.getValue());
        }

        getMarkupSettings().setCompressWhitespace(true);
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        getRequestCycleSettings().setResponseRequestEncoding("UTF-8");

        getExceptionSettings().setUnexpectedExceptionDisplay(
                IExceptionSettings.SHOW_EXCEPTION_PAGE);

        if (plugins != null) {
            for (AbstractPlugin plugin : plugins.values()) {
                plugin.initialize(this);
            }
        }

        ScheduledTaskRegistrar registrar = getScheduledTaskRegistrar();

        List<String> jobs = jdbcTemplate
                .queryForList(
                        "select "
                                + com.itrustcambodia.pluggable.entity.Job.ID
                                + " from "
                                + TableUtilities
                                        .getTableName(com.itrustcambodia.pluggable.entity.Job.class),
                        String.class);

        for (Class<? extends Job> job : getJobs()) {
            com.itrustcambodia.pluggable.entity.Job meta = JobUtilities
                    .createJob(jdbcTemplate, job);
            jobs.remove(meta.getId());
            if (!meta.isDisable()) {
                Scheduled scheduled = job.getAnnotation(Scheduled.class);
                Job bean = null;
                try {
                    bean = job.newInstance();
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                }
                if (bean != null && scheduled != null) {
                    bean.setApplication(this);
                    JobUtilities.register(registrar, scheduled, bean);
                }
            }
        }
        if (jobs != null && !jobs.isEmpty()) {
            for (String job : jobs) {
                jdbcTemplate
                        .update("delete from "
                                + TableUtilities
                                        .getTableName(com.itrustcambodia.pluggable.entity.Job.class)
                                + " where "
                                + com.itrustcambodia.pluggable.entity.Job.ID
                                + " = ?", job);
            }
        }
        registrar.afterPropertiesSet();
    }

    @SuppressWarnings("unchecked")
    private Map<String, AbstractPlugin> getPlugins() {
        Map<String, AbstractPlugin> plugins = (Map<String, AbstractPlugin>) getServletContext()
                .getAttribute("Plugins");
        if (plugins == null) {
            plugins = new HashMap<String, AbstractPlugin>();
            getServletContext().setAttribute("Plugins", plugins);
        }
        return plugins;
    }

    public SchedulerFactory getSchedulerFactory() {
        return getBean(SchedulerFactory.class);
    }

    public void addJob(Class<? extends Job> job) {
        getJobs().add(job);
    }

    public AbstractPlugin getPlugin(String identity) {
        return getPlugins().get(identity);
    }

    public String getRealm() {
        return "Security";
    }

    public String getSecretKey() {
        return DigestUtils.shaHex(this.getClass().getName());
    }

    public void addRole(String name, String description) {
        if (getRoles().containsKey(name)) {
            throw new WicketRuntimeException("role " + name
                    + " is not avaiable");
        }
        getRoles().put(name, description);
    }

    public void addGroup(String name, List<String> roles) {
        if (getGroups().containsKey(name)) {
            throw new WicketRuntimeException("group " + name
                    + " is not avaiable");
        }
        getGroups().put(name, roles);
    }

    public <T> void addBean(Class<T> clazz, T bean) {
        addBean(clazz.getName(), bean);
    }

    public void addBean(String name, Object bean) {
        if (getBeans().containsKey(name)) {
            throw new WicketRuntimeException("bean registry is ambiguation "
                    + bean.getClass().getSimpleName() + " with "
                    + getBeans().get(name).getClass().getSimpleName());
        }
        getBeans().put(name, bean);
    }

    public void addController(String path, Class<?> clazz, Method method) {
        if (getControllers().containsKey(path)) {
            throw new WicketRuntimeException(
                    "controller registry is ambiguation "
                            + clazz.getSimpleName()
                            + " with "
                            + getControllers().get(path).getClazz()
                                    .getSimpleName());
        }
        RequestMappingInfo info = new RequestMappingInfo();
        info.setClazz(clazz);
        info.setMethod(method);
        getControllers().put(path, info);
    }

    public void addPlugin(String identity, AbstractPlugin plugin) {
        getPlugins().put(identity, plugin);
    }

    public void addMount(String path, Class<? extends WebPage> clazz) {
        if (getMounts().containsKey(path)) {
            throw new WicketRuntimeException(
                    "page mount registry is ambiguation "
                            + clazz.getSimpleName() + " with "
                            + getMounts().get(path).getSimpleName());
        }
        getMounts().put(path, clazz);
    }

    public void addPluginMapping(String clazz, String identity) {
        getPluginMapping().put(clazz, identity);
    }

    public String getPluginMapping(String clazz) {
        return getPluginMapping().get(clazz);
    }

    public void update(String identity, String name, Object value) {
        DataSource dataSource = getBean(DataSource.class);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Gson gson = getBean(Gson.class);
        RegistryUtilities.update(gson, jdbcTemplate, identity, name, value);
    }

    public <T> T select(String identity, String name, Class<T> clazz) {
        DataSource dataSource = getBean(DataSource.class);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Gson gson = getBean(Gson.class);
        return RegistryUtilities.select(jdbcTemplate, gson, identity, name,
                clazz);
    }

    public void update(String name, Object value) {
        DataSource dataSource = getBean(DataSource.class);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Gson gson = getBean(Gson.class);
        RegistryUtilities.update(gson, jdbcTemplate, name, value);
    }

    public <T> T select(String name, Class<T> clazz) {
        DataSource dataSource = getBean(DataSource.class);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        Gson gson = getBean(Gson.class);
        return RegistryUtilities.select(jdbcTemplate, gson, name, clazz);
    }

    public List<Menu> getPluginMenus() {
        DataSource dataSource = getBean(DataSource.class);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return FrameworkUtilities.getPluginMenus(jdbcTemplate, getPlugins());
    }

    public double getVersion() {
        AbstractApplicationMigrator migrator = (AbstractApplicationMigrator) getBean(getMigrator()
                .getName());
        return migrator.getVersion();
    }

    public final int getNullType() {
        return nullType;
    }

    @Override
    public RuntimeConfigurationType getConfigurationType() {
        return RuntimeConfigurationType.DEPLOYMENT;
    }

    public final void setNullType(int nullType) {
        this.nullType = nullType;
    }

    public Object getBean(String name) {
        return getBeans().get(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        return (T) this.getBean(clazz.getName());
    }

    public final boolean isMigrated() {
        try {
            ApplicationRegistry applicationRegistry = getJdbcTemplate()
                    .queryForObject(
                            "select * from "
                                    + TableUtilities.getTableName(ApplicationRegistry.class)
                                    + " order by "
                                    + ApplicationRegistry.VERSION
                                    + " desc limit 1",
                            new EntityRowMapper<ApplicationRegistry>(
                                    ApplicationRegistry.class));
            return applicationRegistry.getVersion() == getVersion();
        } catch (BadSqlGrammarException e) {
            return false;
        }
    }

    public List<Menu> getApplicationMenus(Roles roles) {
        return null;
    }

    public String[] getPackages() {
        return new String[] { this.getClass().getPackage().getName() };
    }

    public Class<? extends WebPage> getHomePage() {
        return HomePage.class;
    }

    public Class<? extends WebPage> getDashboardPage() {
        return DashboardPage.class;
    }

    public Class<? extends AbstractUser> getUserEntity() {
        return com.itrustcambodia.pluggable.entity.User.class;
    }

    public Class<? extends WebPage> getNewUserPage() {
        return NewUserPage.class;
    }

    public Class<? extends WebPage> getEditUserPage() {
        return EditUserPage.class;
    }

    public Class<? extends WebPage> getUserManagementPage() {
        return UserManagementPage.class;
    }

    public Class<? extends WebPage> getNewGroupPage() {
        return NewGroupPage.class;
    }

    public Class<? extends WebPage> getEditGroupPage() {
        return EditGroupPage.class;
    }

    public Class<? extends WebPage> getGroupManagementPage() {
        return GroupManagementPage.class;
    }

    public Class<? extends AbstractApplicationMigrator> getMigrator() {
        return ApplicationMigrator.class;
    }

    public Schema getSchema() {
        return getBean(Schema.class);
    }

    public JdbcTemplate getJdbcTemplate() {
        return getBean(JdbcTemplate.class);
    }

    public Class<? extends ApplicationSettingPage> getSettingPage() {
        return SettingPage.class;
    }

    public String getBrandLabel() {
        String label = getServletContext().getServletContextName();
        if (label == null || "".equals(label)) {
            return this.getClass().getSimpleName();
        } else {
            return label;
        }
    }

    public String getPluginLabel() {
        return "Plugins";
    }

    public Gson getGson() {
        return getBean(Gson.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getScheduledTaskRegistrar() != null) {
            getScheduledTaskRegistrar().destroy();
        }
    }
}
