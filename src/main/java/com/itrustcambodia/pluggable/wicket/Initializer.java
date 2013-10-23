package com.itrustcambodia.pluggable.wicket;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.core.request.mapper.ResourceMapper;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.page.WebPage;
import com.itrustcambodia.pluggable.quartz.Job;
import com.itrustcambodia.pluggable.rest.Controller;
import com.itrustcambodia.pluggable.rest.RequestMapping;
import com.itrustcambodia.pluggable.utilities.FrameworkUtilities;

/**
 * @author Socheat KHAUV
 */
public class Initializer implements IInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Initializer.class);

    @Override
    public void init(Application application) {
        LOGGER.info("starting framework initializer");
        if (application instanceof AbstractWebApplication) {
            List<String> packages = new ArrayList<String>();
            packages.add("com.itrustcambodia.pluggable");
            packages.addAll(Arrays.asList(((AbstractWebApplication) application).getPackages()));

            if (packages != null && !packages.isEmpty()) {

                Map<String, String> roles = FrameworkUtilities.lookupRoles(packages.toArray(new String[packages.size()]));
                if (roles != null && !roles.isEmpty()) {
                    for (Entry<String, String> role : roles.entrySet()) {
                        ((AbstractWebApplication) application).addRole(role.getKey(), role.getValue());
                    }
                }

                for (String javaPackage : packages) {
                    Reflections reflections = new Reflections(javaPackage);

                    Set<Class<?>> pages = reflections.getTypesAnnotatedWith(Mount.class);
                    if (pages != null && !pages.isEmpty()) {
                        for (Class<?> page : pages) {
                            Mount mount = page.getAnnotation(Mount.class);
                            if (mount.value().equals("")) {
                                throw new WicketRuntimeException(page.getSimpleName() + " mount page is not allow empty");
                            }
                            if (mount.value().equals("/")) {
                                throw new WicketRuntimeException(page.getSimpleName() + " mount page is not allow with /");
                            }
                            if (mount.value().startsWith("/" + RestController.PATH)) {
                                throw new WicketRuntimeException(page.getSimpleName() + " mount page is not allow start with /" + RestController.PATH);
                            }
                            ((AbstractWebApplication) application).addMount(mount.value(), (Class<? extends WebPage>) page);
                        }
                    }

                    Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
                    if (controllers != null && !controllers.isEmpty()) {
                        for (Class<?> controller : controllers) {
                            for (Method method : ReflectionUtils.getAllMethods(controller)) {
                                if (method.getAnnotation(RequestMapping.class) != null) {
                                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                                    ((AbstractWebApplication) application).addController(requestMapping.value(), controller, method);
                                }
                            }
                        }
                    }

                    Set<Class<? extends Job>> jobs = reflections.getSubTypesOf(Job.class);
                    for (Class<? extends Job> job : jobs) {
                        ((AbstractWebApplication) application).addJob(job);
                    }
                }
            }

            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            ((AbstractWebApplication) application).addBean(SchedulerFactory.class, schedulerFactory);
        }

        ResourceReference reference = new ResourceReference(RestController.class.getName()) {
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
            application.getResourceReferenceRegistry().registerResourceReference(reference);
        }

        application.getRootRequestMapperAsCompound().add(new ResourceMapper("/" + RestController.PATH, reference));
    }

    @Override
    public void destroy(Application application) {
    }

}
