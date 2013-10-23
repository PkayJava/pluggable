//package com.itrustcambodia.pluggable.core;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.joda.time.DateTime;
//import org.joda.time.DateTimeZone;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.BeansException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//import com.itrustcambodia.pluggable.PluggableConstants;
//
//public class SpringMigrationInterceptor extends HandlerInterceptorAdapter implements ApplicationContextAware {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(SpringMigrationInterceptor.class);
//
//    private ApplicationContext applicationContext;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        IApplication application = applicationContext.getBean(IApplication.class);
//
//        if (HandlerMethod.class.isAssignableFrom(handler.getClass())) {
//            HandlerMethod handlerMethod = (HandlerMethod) handler;
//            String contextPath = request.getContextPath();
//            if (contextPath == null || "".equals(contextPath) || contextPath.equals("/")) {
//                contextPath = "/rest";
//            } else {
//                if (contextPath.endsWith("/")) {
//                    contextPath = contextPath + "rest";
//                }
//            }
//            if (!contextPath.startsWith("/")) {
//                contextPath = "/" + contextPath;
//            }
//
//            Class<?> clazz = handlerMethod.getBeanType();
//            String controllerPath = "";
//
//            if (clazz.isAnnotationPresent(RequestMapping.class)) {
//                controllerPath = clazz.getAnnotation(RequestMapping.class).value()[0];
//                if (!controllerPath.startsWith("/")) {
//                    controllerPath = "/" + controllerPath;
//                }
//                if (controllerPath.endsWith("/")) {
//                    controllerPath = controllerPath.substring(0, controllerPath.length() - 1);
//                }
//            }
//
//            RequestMapping requestMapping = handlerMethod.getMethod().getAnnotation(RequestMapping.class);
//
//            String path = requestMapping.value()[0];
//            if (!path.startsWith("/")) {
//                path = "/" + path;
//            }
//            if (path.endsWith("/")) {
//                path = path.substring(0, path.length() - 1);
//            }
//            if (!"".equals(controllerPath)) {
//                path = controllerPath + path;
//            }
//            path = contextPath + path;
//
//            Boolean debug = application.select(PluggableConstants.DEBUG, Boolean.class);
//            if (debug != null && debug) {
//                LOGGER.info("{} : {}", new DateTime().withZone(DateTimeZone.forOffsetHours(7)).toString("yyyy-MM-dd'T'HH:mm:ss"), path);
//            }
//
//        }
//
//        boolean valid = true;
//        if (!application.isMigrated()) {
//            valid = false;
//        }
//
//        boolean deprecated = false;
//        if (HandlerMethod.class.isAssignableFrom(handler.getClass())) {
//            HandlerMethod handlerMethod = (HandlerMethod) handler;
//            if (handlerMethod.getMethodAnnotation(Deprecated.class) != null) {
//                deprecated = true;
//            }
//
//            String clazz = ((HandlerMethod) handler).getBeanType().getName();
//            if (application.getPluginMapping(clazz) != null) {
//                AbstractPlugin plugin = application.getPlugin(application.getPluginMapping(clazz));
//                if (!plugin.isMigrated()) {
//                    valid = false;
//                } else {
//                    if (!plugin.isActivated()) {
//                        valid = false;
//                    }
//                }
//            }
//        }
//
//        if (!valid) {
//            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//        }
//        if (valid) {
//            if (deprecated) {
//                Boolean allowDeprecated = application.select(PluggableConstants.DEPRECATED, Boolean.class);
//                if (allowDeprecated == null || !allowDeprecated) {
//                    response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
//                    return false;
//                }
//            }
//        }
//        return valid;
//    }
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.applicationContext = applicationContext;
//    }
//}
