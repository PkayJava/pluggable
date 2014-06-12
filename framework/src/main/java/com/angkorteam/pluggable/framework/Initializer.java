package com.angkorteam.pluggable.framework;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.angkorteam.pluggable.framework.quartz.AbstractJob;
import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.core.request.mapper.ResourceMapper;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.core.Mount;
import com.angkorteam.pluggable.framework.page.ApplicationSettingPage;
import com.angkorteam.pluggable.framework.page.DashboardPage;
import com.angkorteam.pluggable.framework.page.EditGroupPage;
import com.angkorteam.pluggable.framework.page.EditUserPage;
import com.angkorteam.pluggable.framework.page.GroupManagementPage;
import com.angkorteam.pluggable.framework.page.LoginPage;
import com.angkorteam.pluggable.framework.page.NewGroupPage;
import com.angkorteam.pluggable.framework.page.NewUserPage;
import com.angkorteam.pluggable.framework.page.SettingPage;
import com.angkorteam.pluggable.framework.page.UserManagementPage;
import com.angkorteam.pluggable.framework.page.WebPage;
import com.angkorteam.pluggable.framework.rest.Controller;
import com.angkorteam.pluggable.framework.rest.RequestMapping;
import com.angkorteam.pluggable.framework.utilities.FrameworkUtilities;
import com.angkorteam.pluggable.framework.wicket.RestController;

import javax.persistence.Entity;

/**
 * @author Socheat KHAUV
 */
public class Initializer implements IInitializer {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(Initializer.class);

    @Override
    @SuppressWarnings("unchecked")
    public void init(Application application) {
        LOGGER.info("starting framework initializer");
        if (application instanceof AbstractWebApplication) {
            List<String> packages = new ArrayList<String>();
            packages.add(Initializer.class.getPackage().getName());
            packages.addAll(Arrays
                    .asList(((AbstractWebApplication) application)
                            .getPackages()));

            if (packages != null && !packages.isEmpty()) {

                Map<String, String> roles = FrameworkUtilities
                        .lookupRoles(packages.toArray(new String[packages
                                .size()]));
                if (roles != null && !roles.isEmpty()) {
                    for (Entry<String, String> role : roles.entrySet()) {
                        ((AbstractWebApplication) application).addRole(
                                role.getKey(), role.getValue());
                    }
                }

                for (String javaPackage : packages) {
                    Reflections reflections = new Reflections(javaPackage);

                    Set<Class<?>> pages = reflections
                            .getTypesAnnotatedWith(Mount.class);

                    if (pages != null && !pages.isEmpty()) {
                        for (Class<?> page : pages) {
                            Mount mount = null;
                            if (page == ((AbstractWebApplication) application)
                                    .getUserManagementPage()
                                    || page == ((AbstractWebApplication) application)
                                            .getNewUserPage()
                                    || page == ((AbstractWebApplication) application)
                                            .getEditUserPage()
                                    || page == ((AbstractWebApplication) application)
                                            .getGroupManagementPage()
                                    || page == ((AbstractWebApplication) application)
                                            .getNewGroupPage()
                                    || page == ((AbstractWebApplication) application)
                                            .getEditGroupPage()
                                    || page == ((AbstractWebApplication) application)
                                            .getDashboardPage()
                                    || page == ((AbstractWebApplication) application)
                                            .getSignInPageClass()
                                    || page == ((AbstractWebApplication) application)
                                            .getSettingPage()) {
                                if (page == UserManagementPage.class
                                        || page == NewUserPage.class
                                        || page == EditUserPage.class
                                        || page == GroupManagementPage.class
                                        || page == NewGroupPage.class
                                        || page == EditGroupPage.class
                                        || page == DashboardPage.class
                                        || page == LoginPage.class
                                        || page == ApplicationSettingPage.class) {
                                    if (javaPackage.equals(Initializer.class
                                            .getPackage().getName())) {
                                        mount = page.getAnnotation(Mount.class);
                                    } else {
                                        continue;
                                    }
                                } else {
                                    continue;
                                }

                            } else if (page == UserManagementPage.class
                                    || page == NewUserPage.class) {
                                if (page == UserManagementPage.class) {
                                    if (((AbstractWebApplication) application)
                                            .getUserManagementPage()
                                            .isAnnotationPresent(Mount.class)) {
                                        mount = ((AbstractWebApplication) application)
                                                .getUserManagementPage()
                                                .getAnnotation(Mount.class);
                                    } else {
                                        mount = UserManagementPage.class
                                                .getAnnotation(Mount.class);
                                    }
                                } else if (page == NewUserPage.class) {
                                    if (((AbstractWebApplication) application)
                                            .getNewUserPage()
                                            .isAnnotationPresent(Mount.class)) {
                                        mount = ((AbstractWebApplication) application)
                                                .getNewUserPage()
                                                .getAnnotation(Mount.class);
                                    } else {
                                        mount = NewUserPage.class
                                                .getAnnotation(Mount.class);
                                    }
                                } else if (page == EditUserPage.class) {
                                    if (((AbstractWebApplication) application)
                                            .getEditUserPage()
                                            .isAnnotationPresent(Mount.class)) {
                                        mount = ((AbstractWebApplication) application)
                                                .getEditUserPage()
                                                .getAnnotation(Mount.class);
                                    } else {
                                        mount = EditUserPage.class
                                                .getAnnotation(Mount.class);
                                    }
                                } else if (page == GroupManagementPage.class) {
                                    if (((AbstractWebApplication) application)
                                            .getGroupManagementPage()
                                            .isAnnotationPresent(Mount.class)) {
                                        mount = ((AbstractWebApplication) application)
                                                .getGroupManagementPage()
                                                .getAnnotation(Mount.class);
                                    } else {
                                        mount = GroupManagementPage.class
                                                .getAnnotation(Mount.class);
                                    }
                                } else if (page == NewGroupPage.class) {
                                    if (((AbstractWebApplication) application)
                                            .getNewGroupPage()
                                            .isAnnotationPresent(Mount.class)) {
                                        mount = ((AbstractWebApplication) application)
                                                .getNewGroupPage()
                                                .getAnnotation(Mount.class);
                                    } else {
                                        mount = NewGroupPage.class
                                                .getAnnotation(Mount.class);
                                    }
                                } else if (page == EditGroupPage.class) {
                                    if (((AbstractWebApplication) application)
                                            .getEditGroupPage()
                                            .isAnnotationPresent(Mount.class)) {
                                        mount = ((AbstractWebApplication) application)
                                                .getEditGroupPage()
                                                .getAnnotation(Mount.class);
                                    } else {
                                        mount = EditGroupPage.class
                                                .getAnnotation(Mount.class);
                                    }
                                } else if (page == DashboardPage.class) {
                                    if (((AbstractWebApplication) application)
                                            .getDashboardPage()
                                            .isAnnotationPresent(Mount.class)) {
                                        mount = ((AbstractWebApplication) application)
                                                .getDashboardPage()
                                                .getAnnotation(Mount.class);
                                    } else {
                                        mount = DashboardPage.class
                                                .getAnnotation(Mount.class);
                                    }
                                } else if (page == LoginPage.class) {
                                    if (((AbstractWebApplication) application)
                                            .getSignInPageClass()
                                            .isAnnotationPresent(Mount.class)) {
                                        mount = ((AbstractWebApplication) application)
                                                .getSignInPageClass()
                                                .getAnnotation(Mount.class);
                                    } else {
                                        mount = LoginPage.class
                                                .getAnnotation(Mount.class);
                                    }
                                } else if (page == ApplicationSettingPage.class) {
                                    if (((AbstractWebApplication) application)
                                            .getSettingPage()
                                            .isAnnotationPresent(Mount.class)) {
                                        mount = ((AbstractWebApplication) application)
                                                .getSettingPage()
                                                .getAnnotation(Mount.class);
                                    } else {
                                        mount = SettingPage.class
                                                .getAnnotation(Mount.class);
                                    }
                                } else {
                                    continue;
                                }
                            } else {
                                mount = page.getAnnotation(Mount.class);
                            }
                            if (mount.value().equals("")) {
                                throw new WicketRuntimeException(
                                        page.getSimpleName()
                                                + " mount page is not allow empty");
                            }
                            if (mount.value().equals("/")) {
                                throw new WicketRuntimeException(
                                        page.getSimpleName()
                                                + " mount page is not allow with /");
                            }
                            if (mount.value().startsWith(
                                    "/" + RestController.PATH)) {
                                throw new WicketRuntimeException(
                                        page.getSimpleName()
                                                + " mount page is not allow start with /"
                                                + RestController.PATH);
                            }

                            if (page == UserManagementPage.class) {
                                ((AbstractWebApplication) application)
                                        .addMount(
                                                mount.value(),
                                                ((AbstractWebApplication) application)
                                                        .getUserManagementPage());
                            } else if (page == NewUserPage.class) {
                                ((AbstractWebApplication) application)
                                        .addMount(
                                                mount.value(),
                                                ((AbstractWebApplication) application)
                                                        .getNewUserPage());
                            } else if (page == EditUserPage.class) {
                                ((AbstractWebApplication) application)
                                        .addMount(
                                                mount.value(),
                                                ((AbstractWebApplication) application)
                                                        .getEditUserPage());
                            } else if (page == GroupManagementPage.class) {
                                ((AbstractWebApplication) application)
                                        .addMount(
                                                mount.value(),
                                                ((AbstractWebApplication) application)
                                                        .getGroupManagementPage());
                            } else if (page == NewGroupPage.class) {
                                ((AbstractWebApplication) application)
                                        .addMount(
                                                mount.value(),
                                                ((AbstractWebApplication) application)
                                                        .getNewGroupPage());
                            } else if (page == EditGroupPage.class) {
                                ((AbstractWebApplication) application)
                                        .addMount(
                                                mount.value(),
                                                ((AbstractWebApplication) application)
                                                        .getEditGroupPage());
                            } else if (page == DashboardPage.class) {
                                ((AbstractWebApplication) application)
                                        .addMount(
                                                mount.value(),
                                                ((AbstractWebApplication) application)
                                                        .getDashboardPage());
                            } else if (page == LoginPage.class) {
                                ((AbstractWebApplication) application)
                                        .addMount(
                                                mount.value(),
                                                ((AbstractWebApplication) application)
                                                        .getSignInPageClass());
                            } else {
                                ((AbstractWebApplication) application)
                                        .addMount(mount.value(),
                                                (Class<? extends WebPage>) page);
                            }
                        }
                    }
                    Set<Class<?>> controllers = reflections
                            .getTypesAnnotatedWith(Controller.class);
                    if (controllers != null && !controllers.isEmpty()) {
                        for (Class<?> controller : controllers) {
                            for (Method method : ReflectionUtils
                                    .getAllMethods(controller)) {
                                if (method.getAnnotation(RequestMapping.class) != null) {
                                    RequestMapping requestMapping = method
                                            .getAnnotation(RequestMapping.class);
                                    ((AbstractWebApplication) application)
                                            .addController(
                                                    requestMapping.value(),
                                                    controller, method);
                                }
                            }
                        }
                    }

                    Set<Class<? extends AbstractJob>> jobs = reflections
                            .getSubTypesOf(AbstractJob.class);
                    for (Class<? extends AbstractJob> job : jobs) {
                        if (!Modifier.isAbstract(job.getModifiers())
                                && job.isAnnotationPresent(com.angkorteam.pluggable.framework.quartz.Scheduled.class)) {
                            ((AbstractWebApplication) application).addJob(job);
                        }
                    }

                    Set<Class<?>> entities = (Set<Class<?>>) reflections
                            .getTypesAnnotatedWith(Entity.class);
                    if (entities != null && !entities.isEmpty()) {
                        for (Class<?> entity : entities) {
                            ((AbstractWebApplication) application).addEntity((Class<? extends Serializable>)entity);
                        }
                    }
                }
            }
        }

        ResourceReference reference = new ResourceReference(
                RestController.class.getName()) {
            /**
             * 
             */
            private static final long serialVersionUID = 3172575738858464207L;

            RestController controller = new RestController();

            @Override
            public IResource getResource() {
                return controller;
            }
        };

        if (reference.canBeRegistered()) {
            application.getResourceReferenceRegistry()
                    .registerResourceReference(reference);
        }

        application.getRootRequestMapperAsCompound().add(
                new ResourceMapper("/" + RestController.PATH, reference));
    }

    @Override
    public void destroy(Application application) {
    }

}
