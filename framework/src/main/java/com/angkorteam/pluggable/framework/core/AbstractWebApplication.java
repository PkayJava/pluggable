package com.angkorteam.pluggable.framework.core;

import com.angkorteam.pluggable.framework.database.DbSupport;
import com.angkorteam.pluggable.framework.database.DbSupportFactory;
import com.angkorteam.pluggable.framework.database.Schema;
import com.angkorteam.pluggable.framework.entity.AbstractUser;
import com.angkorteam.pluggable.framework.entity.ApplicationRegistry;
import com.angkorteam.pluggable.framework.entity.Group;
import com.angkorteam.pluggable.framework.entity.Role;
import com.angkorteam.pluggable.framework.mapper.ApplicationRegistryMapper;
import com.angkorteam.pluggable.framework.migration.AbstractApplicationMigrator;
import com.angkorteam.pluggable.framework.migration.AbstractPluginMigrator;
import com.angkorteam.pluggable.framework.migration.ApplicationMigrator;
import com.angkorteam.pluggable.framework.page.*;
import com.angkorteam.pluggable.framework.quartz.AbstractJob;
import com.angkorteam.pluggable.framework.quartz.Scheduled;
import com.angkorteam.pluggable.framework.utilities.*;
import com.angkorteam.pluggable.framework.wicket.RequestMappingInfo;
import com.angkorteam.pluggable.framework.wicket.authroles.authentication.AuthenticatedWebApplication;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.Roles;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.settings.IExceptionSettings;
import org.jongo.Jongo;
import org.quartz.SchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.persistence.*;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

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

    public static final String DATABASE_TYPE_MONGODB = "MongoDB";

    public static final String DATABASE_TYPE_MYSQL = "MySQL";

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
            ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
            executor.setDaemon(true);
            executor.setPoolSize(10);
            executor.setThreadNamePrefix(getServletContext()
                    .getServletContextName() + "-Thread-");
            executor.afterPropertiesSet();
            getServletContext().setAttribute("ThreadPoolTaskScheduler",
                    executor);

            registrar = new ScheduledTaskRegistrar();
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

    private List<Class<? extends AbstractJob>> getJobs() {
        List<Class<? extends AbstractJob>> jobs = (List<Class<? extends AbstractJob>>) getServletContext()
                .getAttribute("Jobs");
        if (jobs == null) {
            jobs = new ArrayList<Class<? extends AbstractJob>>();
            getServletContext().setAttribute("Jobs", jobs);
        }
        return jobs;
    }

    private List<Class<? extends Serializable>> getEntities() {
        List<Class<? extends Serializable>> entities = (List<Class<? extends Serializable>>) getServletContext()
                .getAttribute("Entities");
        if (entities == null) {
            entities = new ArrayList<Class<? extends Serializable>>();
            getServletContext().setAttribute("Entities", entities);
        }
        return entities;
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

    protected abstract String getDriverClass();

    protected abstract String getJdbcUrl();

    protected abstract String getJdbcUsername();

    protected abstract String getJdbcPassword();

    protected abstract void initDatabaseSetting();

    @Override
    protected final void init() {
        super.init();
        LOGGER.info("starting application framework");
        initDatabaseSetting();

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(getDriverClass());
        dataSource.setUrl(getJdbcUrl());
        dataSource.setUsername(getJdbcUsername());
        dataSource.setPassword(getJdbcPassword());
        dataSource.setInitialSize(5);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestWhileIdle(true);
        addBean(DataSource.class, dataSource);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        addBean(JdbcTemplate.class, jdbcTemplate);
        DbSupport support = DbSupportFactory.createDbSupport(jdbcTemplate);
        addBean(DbSupport.class, support);
        Schema schema = support.getSchema();
        addBean(Schema.class, schema);

        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting()
                .create();
        addBean(Gson.class, gson);

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

        List<String> pooledRoles = jdbcTemplate.queryForList(
                "select " + Role.NAME + " from "
                        + TableUtilities.getTableName(Role.class),
                String.class);

        Map<String, Role> mapping = new HashMap<String, Role>();

        Group adminGroup = GroupUtilities.createJdbcGroup(jdbcTemplate,
                AbstractWebApplication.SUPER_ADMIN_GROUP,
                AbstractWebApplication.SUPER_ADMIN_GROUP, false);
        if (getRoles() != null && !getRoles().isEmpty()) {
            for (Entry<String, String> role : getRoles().entrySet()) {
                pooledRoles.remove(role.getKey());
                mapping.put(role.getKey(), RoleUtilities.createJdbcRole(
                        getBean(DataSource.class), role.getKey(),
                        role.getValue(), false));
                SecurityUtilities.grantJdbcAccess(jdbcTemplate, adminGroup,
                        mapping.get(role.getKey()));
            }
        }

        FrameworkUtilities.initExceptionPages(this);

        if (!pooledRoles.isEmpty()) {
            for (String pooledRole : pooledRoles) {
                RoleUtilities.removeJdbcRole(jdbcTemplate, pooledRole);
            }
            pooledRoles.clear();
        }

        for (Entry<String, List<String>> roles : getGroups().entrySet()) {
            Group group = GroupUtilities.createJdbcGroup(jdbcTemplate,
                    roles.getKey(), getPlugin(roles.getKey()).getName(),
                    false);
            for (String role : roles.getValue()) {
                SecurityUtilities.grantJdbcAccess(jdbcTemplate, group,
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
                                + com.angkorteam.pluggable.framework.entity.Job.ID
                                + " from "
                                + TableUtilities
                                .getTableName(com.angkorteam.pluggable.framework.entity.Job.class),
                        String.class);

        for (Class<? extends AbstractJob> job : getJobs()) {
            com.angkorteam.pluggable.framework.entity.Job meta = JobUtilities
                    .createJdbcJob(jdbcTemplate, job);
            jobs.remove(meta.getId());
            if (!meta.isDisable()) {
                Scheduled scheduled = job.getAnnotation(Scheduled.class);
                AbstractJob bean = null;
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
                                .getTableName(com.angkorteam.pluggable.framework.entity.Job.class)
                                + " where "
                                + com.angkorteam.pluggable.framework.entity.Job.ID
                                + " = ?", job);
            }
        }
        registrar.afterPropertiesSet();

        EntityManagerFactory entityManagerFactory = initEntityManagerFactory();
        if (entityManagerFactory != null) {
            addBean(EntityManagerFactory.class, entityManagerFactory);
        }

        doInit();
    }

    protected EntityManagerFactory initEntityManagerFactory() {

        ServletContext servletContext = ((WebApplication) this).getServletContext();

        StringBuffer persistence = new StringBuffer();
        persistence.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("\n");
        persistence.append("<persistence xmlns=\"http://java.sun.com/xml/ns/persistence\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd\" version=\"2.0\">").append("\n");
        persistence.append("\t").append("<persistence-unit name=\"jpa\" transaction-type=\"RESOURCE_LOCAL\">").append("\n");
        persistence.append("\t\t").append("<provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>").append("\n");

        for (Class<?> clazz : getEntities()) {
            if (!clazz.isAnnotationPresent(Exclude.class)) {
                persistence.append("\t\t").append("<class>" + clazz.getName() + "</class>").append("\n");
            }
        }

        persistence.append("\t\t").append("<exclude-unlisted-classes>true</exclude-unlisted-classes>").append("\n");

        persistence.append("\t\t").append("<properties>").append("\n");
        persistence.append("\t\t\t").append("<property name=\"hibernate.dialect\" value=\"org.hibernate.dialect.MySQLMyISAMDialect\"/>").append("\n");
        // persistence.append("\t\t\t").append("<property name=\"hibernate.hbm2ddl.auto\" value=\"none\"/>").append("\n");
        persistence.append("\t\t\t").append("<property name=\"javax.persistence.jdbc.driver\" value=\"" + getDriverClass() + "\"/>").append("\n");
        persistence.append("\t\t\t").append("<property name=\"javax.persistence.jdbc.url\" value=\"" + StringEscapeUtils.escapeXml10(getJdbcUrl()) + "\"/>").append("\n");
        persistence.append("\t\t\t").append("<property name=\"javax.persistence.jdbc.user\" value=\"" + StringEscapeUtils.escapeXml10(getJdbcUsername()) + "\"/>").append("\n");
        persistence.append("\t\t\t").append("<property name=\"javax.persistence.jdbc.password\" value=\"" + StringEscapeUtils.escapeXml10(getJdbcPassword()) + "\"/>").append("\n");

        persistence.append("\t\t").append("</properties>").append("\n");
        persistence.append("\t").append("</persistence-unit>").append("\n");
        persistence.append("").append("</persistence>").append("\n");

        File root = new File(servletContext.getRealPath("/"));

        {
            File webinfo = new File(root, "WEB-INF");
            if (!webinfo.exists()) {
                webinfo.mkdir();
            }

            File classes = new File(webinfo, "classes");
            if (!classes.exists()) {
                classes.mkdir();
            }

            File metainf = new File(classes, "META-INF");
            if (!metainf.exists()) {
                metainf.mkdir();
            }

            File file = new File(metainf, "persistence.xml");
            try {
                FileUtils.write(file, persistence.toString(), false);
            } catch (IOException e) {
            }
        }

        {
            File metainf = new File(root, "META-INF");
            if (!metainf.exists()) {
                metainf.mkdir();
            }

            File file = new File(metainf, "persistence.xml");
            try {
                FileUtils.write(file, persistence.toString(), false);
            } catch (IOException e) {
            }
        }

        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");
            return emf;
        } catch (PersistenceException e) {
            LOGGER.info("persistence error due to {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
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

    public void addJob(Class<? extends AbstractJob> job) {
        getJobs().add(job);
    }

    public void addEntity(Class<? extends Serializable> job) {
        getEntities().add(job);
    }

    public AbstractPlugin getPlugin(String identity) {
        return getPlugins().get(identity);
    }

    public String getRealm() {
        return "Security";
    }

    public String getSecretKey() {
        return DigestUtils.sha1Hex(this.getClass().getName());
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

    public EntityManager getEntityManager() {
        EntityManagerFactory entityManagerFactory = getBean(EntityManagerFactory.class);
        if (entityManagerFactory != null) {
            return entityManagerFactory.createEntityManager();
        }
        return null;
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
                            new ApplicationRegistryMapper());
            return applicationRegistry.getVersion() == getVersion();
        } catch (BadSqlGrammarException e) {
            return false;
        }
    }

    public List<Menu> getApplicationMenus(Roles roles) {
        return null;
    }

    public String[] getPackages() {
        return new String[]{this.getClass().getPackage().getName()};
    }

    public Class<? extends WebPage> getHomePage() {
        return HomePage.class;
    }

    public Class<? extends WebPage> getDashboardPage() {
        return DashboardPage.class;
    }

    public Class<? extends AbstractUser> getUserEntity() {
        return com.angkorteam.pluggable.framework.entity.User.class;
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

    public Jongo getJongo() {
        return getBean(Jongo.class);
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
    public void internalDestroy() {
        try {
            super.internalDestroy();
        } catch (Throwable e) {
            onDestroy();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            if (getServletContext().getAttribute("ThreadPoolTaskScheduler") != null) {
                ThreadPoolTaskScheduler executor = (ThreadPoolTaskScheduler) getServletContext()
                        .getAttribute("ThreadPoolTaskScheduler");
                executor.setWaitForTasksToCompleteOnShutdown(false);
                executor.destroy();
            }

            if (getScheduledTaskRegistrar() != null) {
                getScheduledTaskRegistrar().destroy();
            }
            ((BasicDataSource) getBean(DataSource.class)).close();

            EntityManagerFactory entityManagerFactory = getBean(EntityManagerFactory.class);
            if (entityManagerFactory != null) {
                entityManagerFactory.close();
            }

            Enumeration<Driver> drivers = DriverManager.getDrivers();
            Driver d = null;
            while (drivers.hasMoreElements()) {
                try {
                    d = drivers.nextElement();
                    DriverManager.deregisterDriver(d);
                    LOGGER.info("Driver {} deregistered", d);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            try {
                Class<?> cls = Class
                        .forName("com.mysql.jdbc.AbandonedConnectionCleanupThread");
                Method mth = (cls == null ? null : cls.getMethod("shutdown"));
                if (mth != null) {
                    LOGGER.info("MySQL connection cleanup thread shutdown");
                    mth.invoke(null);
                    LOGGER.info("MySQL connection cleanup thread shutdown successful");
                }
            } catch (Throwable thr) {
                thr.printStackTrace();
            }

            doDestroy();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected abstract void doDestroy();

    protected abstract void doInit();
}
