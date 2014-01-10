package com.itrustcambodia.pluggable.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.apache.wicket.WicketRuntimeException;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.springframework.dao.EmptyResultDataAccessException;

import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.entity.PluginRegistry;
import com.itrustcambodia.pluggable.migration.AbstractPluginMigrator;
import com.itrustcambodia.pluggable.migration.PluginMigrator;
import com.itrustcambodia.pluggable.page.KnownPage;
import com.itrustcambodia.pluggable.page.PluginSettingPage;
import com.itrustcambodia.pluggable.page.WebPage;
import com.itrustcambodia.pluggable.quartz.Job;
import com.itrustcambodia.pluggable.rest.Controller;
import com.itrustcambodia.pluggable.rest.RequestMapping;
import com.itrustcambodia.pluggable.utilities.FrameworkUtilities;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.wicket.RestController;

/**
 * @author Socheat KHAUV
 */
public abstract class AbstractPlugin implements IInitializer {

    private AbstractWebApplication application;

    public abstract String getName();

    public abstract boolean activate(AbstractWebApplication application);

    public abstract void initialize(AbstractWebApplication application);

    public abstract void deactivate();

    public String getIdentity() {
        return this.getClass().getName().toLowerCase();
    }

    public abstract Class<? extends PluginSettingPage> getSettingPage();

    public abstract Class<? extends WebPage> getDashboardPage();

    public Class<? extends AbstractPluginMigrator> getMigrator() {
        return PluginMigrator.class;
    }

    public String[] getPackages() {
        return new String[] { this.getClass().getPackage().getName() };
    }

    public final boolean isMigrated() {
        AbstractPluginMigrator migrator = (AbstractPluginMigrator) getApplication()
                .getBean(getMigrator().getName());
        String identity = getIdentity();
        PluginRegistry pluginRegistry = null;
        try {
            pluginRegistry = application.getJdbcTemplate().queryForObject(
                    "select * from "
                            + TableUtilities.getTableName(PluginRegistry.class)
                            + " where " + PluginRegistry.IDENTITY + " = ?",
                    new EntityRowMapper<PluginRegistry>(PluginRegistry.class),
                    identity);
            return migrator.getVersion() == pluginRegistry.getVersion();
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public final boolean isActivated() {
        PluginRegistry pluginRegistry = null;
        try {
            pluginRegistry = application.getJdbcTemplate().queryForObject(
                    "select * from "
                            + TableUtilities.getTableName(PluginRegistry.class)
                            + " where " + PluginRegistry.IDENTITY + " = ?",
                    new EntityRowMapper<PluginRegistry>(PluginRegistry.class),
                    getIdentity());
            return pluginRegistry.isActivated();
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public void init(Application application) {
        if (application instanceof AbstractWebApplication) {
            this.application = (AbstractWebApplication) application;
            ((AbstractWebApplication) application).addPlugin(getIdentity(),
                    this);
            String[] packages = getPackages();

            Map<String, String> roles = null;
            List<String> groups = new ArrayList<String>();
            if (packages != null && packages.length > 0) {

                roles = FrameworkUtilities.lookupRoles(packages);
                if (roles != null && !roles.isEmpty()) {
                    for (Entry<String, String> role : roles.entrySet()) {
                        ((AbstractWebApplication) application).addRole(
                                role.getKey(), role.getValue());
                        groups.add(role.getKey());
                    }
                }

                for (String javaPackage : getPackages()) {
                    Reflections reflections = new Reflections(javaPackage);

                    Set<Class<?>> controllers = reflections
                            .getTypesAnnotatedWith(Controller.class);
                    if (controllers != null && !controllers.isEmpty()) {
                        for (Class<?> controller : controllers) {
                            getApplication().addPluginMapping(
                                    controller.getName(), getIdentity());
                            for (Method method : ReflectionUtils
                                    .getAllMethods(controller)) {
                                if (method.getAnnotation(RequestMapping.class) != null) {
                                    RequestMapping requestMapping = method
                                            .getAnnotation(RequestMapping.class);
                                    getApplication().addController(
                                            requestMapping.value(), controller,
                                            method);
                                }
                            }
                        }
                    }

                    Set<Class<? extends WebPage>> webPages = reflections
                            .getSubTypesOf(WebPage.class);
                    if (webPages != null && !webPages.isEmpty()) {
                        for (Class<?> webPage : webPages) {
                            String name = webPage.getName();
                            getApplication().addPluginMapping(name,
                                    getIdentity());
                        }
                    }

                    Set<Class<? extends PluginSettingPage>> pluginSettingPages = reflections
                            .getSubTypesOf(PluginSettingPage.class);
                    if (pluginSettingPages != null
                            && !pluginSettingPages.isEmpty()) {
                        for (Class<?> pluginSettingPage : pluginSettingPages) {
                            String name = pluginSettingPage.getName();
                            getApplication().addPluginMapping(name,
                                    getIdentity());
                        }
                    }

                    Set<Class<? extends KnownPage>> knownPages = reflections
                            .getSubTypesOf(KnownPage.class);
                    if (knownPages != null && !knownPages.isEmpty()) {
                        for (Class<?> knownPage : knownPages) {
                            String name = knownPage.getName();
                            getApplication().addPluginMapping(name,
                                    getIdentity());
                        }
                    }

                    Set<Class<?>> mounts = reflections
                            .getTypesAnnotatedWith(Mount.class);
                    if (mounts != null && !mounts.isEmpty()) {
                        for (Class<?> mount : mounts) {
                            Mount meta = mount.getAnnotation(Mount.class);
                            if (meta.value().equals("")) {
                                throw new WicketRuntimeException(
                                        mount.getSimpleName()
                                                + " mount page is not allow empty");
                            }
                            if (meta.value().equals("/")) {
                                throw new WicketRuntimeException(
                                        mount.getSimpleName()
                                                + " mount page is not allow with /");
                            }
                            if (meta.value().startsWith(
                                    "/" + RestController.PATH)) {
                                throw new WicketRuntimeException(
                                        mount.getSimpleName()
                                                + " mount page is not allow start with /"
                                                + RestController.PATH);
                            }
                            ((AbstractWebApplication) application).addMount(
                                    meta.value(),
                                    (Class<? extends WebPage>) mount);
                        }
                    }

                    Set<Class<? extends Job>> jobs = reflections
                            .getSubTypesOf(Job.class);
                    for (Class<? extends Job> job : jobs) {
                        ((AbstractWebApplication) application).addJob(job);
                        getApplication().addPluginMapping(job.getName(),
                                getIdentity());
                    }
                }
            }

            ((AbstractWebApplication) application).addGroup(getIdentity(),
                    groups);

            getApplication().addPluginMapping(getMigrator().getName(),
                    getIdentity());
        }
    }

    @Override
    public void destroy(Application application) {
    }

    public AbstractWebApplication getApplication() {
        return application;
    }

    public void setApplication(AbstractWebApplication application) {
        this.application = application;
    }

}
