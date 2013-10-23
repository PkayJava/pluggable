package com.itrustcambodia.pluggable.utilities;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.doc.ApiError;
import com.itrustcambodia.pluggable.doc.ApiHeader;
import com.itrustcambodia.pluggable.doc.ApiMethod;
import com.itrustcambodia.pluggable.doc.ApiObject;
import com.itrustcambodia.pluggable.doc.ApiObjectField;
import com.itrustcambodia.pluggable.doc.ApiParam;
import com.itrustcambodia.pluggable.form.ObjectAPIForm;
import com.itrustcambodia.pluggable.form.RestAPIForm;
import com.itrustcambodia.pluggable.rest.RequestMapping;
import com.itrustcambodia.pluggable.wicket.RequestMappingInfo;
import com.itrustcambodia.pluggable.wicket.RestController;
import com.itrustcambodia.pluggable.wicket.authroles.Role;
import com.itrustcambodia.pluggable.wicket.authroles.Secured;

public class RestDocUtilities {

    private RestDocUtilities() {
    }

    public static final String getClassType(Type type, Stack<Class<?>> queueForm) {
        if (type instanceof Class<?>) {
            Class<?> clazz = (Class<?>) type;
            if (clazz == List.class || clazz.isArray()) {
                return "[]";
            } else if (clazz == Void.class || clazz == Null.class) {
                return "null";
            } else {
                String name = "";
                if (clazz.isAnnotationPresent(ApiObject.class)) {
                    queueForm.push(clazz);
                    name = clazz.getName();
                } else {
                    name = clazz.getSimpleName();
                }
                return name;
            }
        } else if (type instanceof GenericArrayType) {
            Type temp = ((GenericArrayType) type).getGenericComponentType();
            return getClassType(temp, queueForm) + "[]";
        } else if (type instanceof WildcardType) {
            return "*";
        } else {
            List<String> clazzs = new ArrayList<String>();
            ParameterizedType parameterizedType = (ParameterizedType) type;
            String result = "";
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            for (Type tmp : parameterizedType.getActualTypeArguments()) {
                clazzs.add(getClassType(tmp, queueForm));
            }
            if (rawType.isArray() || rawType == List.class) {
                result = StringUtils.join(clazzs, ",") + "[]";
            } else {
                String name = "";
                if (rawType.isAnnotationPresent(ApiObject.class)) {
                    queueForm.push(rawType);
                    name = rawType.getName();
                } else {
                    name = rawType.getSimpleName();
                }
                result = name + "<" + StringUtils.join(clazzs, ",") + ">";
            }
            return result;
        }
    }

    public static final void fillRestAPI(HttpServletRequest request, AbstractWebApplication application, List<RestAPIForm> restAPIForms, List<ObjectAPIForm> objectAPIForms) {

        Stack<Class<?>> queueForm = new Stack<Class<?>>();

        String contextPath = request.getContextPath();
        if (contextPath == null || "".equals(contextPath) || contextPath.equals("/")) {
            contextPath = "/" + RestController.PATH;
        } else {
            if (contextPath.endsWith("/")) {
                contextPath = contextPath + RestController.PATH;
            } else {
                contextPath = contextPath + "/" + RestController.PATH;
            }
        }
        if (!contextPath.startsWith("/")) {
            contextPath = "/" + contextPath;
        }

        for (Entry<String, RequestMappingInfo> info : application.getControllers().entrySet()) {

            Method method = info.getValue().getMethod();

            if (method.isAnnotationPresent(RequestMapping.class) && method.isAnnotationPresent(ApiMethod.class)) {
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                ApiMethod apiMethod = method.getAnnotation(ApiMethod.class);
                String path = requestMapping.value();
                if (path.endsWith("/")) {
                    path = path.substring(0, path.length() - 1);
                }
                path = contextPath + path.replace(":.*}", "}");
                RestAPIForm restAPIForm = new RestAPIForm();
                restAPIForm.setDeprecated(method.isAnnotationPresent(Deprecated.class));
                restAPIForm.setPath(path);
                restAPIForm.setDescription(apiMethod.description());

                if (method.isAnnotationPresent(Secured.class)) {
                    Secured secured = method.getAnnotation(Secured.class);
                    List<String> roles = new ArrayList<String>();
                    for (Role role : secured.roles()) {
                        roles.add(role.name());
                    }
                    restAPIForm.setRoles(roles.toArray(new String[roles.size()]));
                } else {
                    restAPIForm.setRoles(new String[] {});
                }

                restAPIForm.setMethod(requestMapping.method());

                List<List<Map<String, String>>> formParameters = new ArrayList<List<Map<String, String>>>();
                if (apiMethod.requestParameters() != null && apiMethod.requestParameters().length > 0) {
                    for (ApiParam apiParam : apiMethod.requestParameters()) {
                        List<Map<String, String>> formParameter = new ArrayList<Map<String, String>>();
                        if (apiParam.description() != null && !"".equals(apiParam.description())) {
                            Map<String, String> name = new HashMap<String, String>();
                            name.put("name", apiParam.name());
                            name.put("value", apiParam.description());
                            formParameter.add(name);

                            Map<String, String> type = new HashMap<String, String>();
                            type.put("name", "");
                            if (apiParam.type() == Null.class) {
                                type.put("value", "Type: String");
                            } else {
                                type.put("value", "Type: " + apiParam.type().getSimpleName());
                            }
                            formParameter.add(type);

                        } else {
                            Map<String, String> name = new HashMap<String, String>();
                            name.put("name", apiParam.name());
                            name.put("value", "Type: " + apiParam.type().getSimpleName());
                            formParameter.add(name);
                        }
                        Map<String, String> required = new HashMap<String, String>();
                        required.put("name", "");
                        required.put("value", "Required: " + String.valueOf(apiParam.required()));
                        formParameter.add(required);

                        if (apiParam.allowedvalues() != null && apiParam.allowedvalues().length > 0) {
                            Map<String, String> allowedvalues = new HashMap<String, String>();
                            allowedvalues.put("name", "");
                            allowedvalues.put("value", "Allowed values: " + StringUtils.join(apiParam.allowedvalues(), ","));
                            formParameter.add(allowedvalues);
                        }
                        if (apiParam.format() != null && !"".equals(apiParam.format())) {
                            Map<String, String> format = new HashMap<String, String>();
                            format.put("name", "");
                            format.put("value", "Format: " + apiParam.format());
                            formParameter.add(format);
                        }

                        formParameters.add(formParameter);
                    }
                }
                restAPIForm.setFormParameters(formParameters);

                List<Map<String, String>> headers = new ArrayList<Map<String, String>>();
                if (apiMethod.headers() != null && apiMethod.headers().length > 0) {
                    for (ApiHeader apiHeader : apiMethod.headers()) {
                        Map<String, String> header = new HashMap<String, String>();
                        header.put("name", apiHeader.name());
                        header.put("description", apiHeader.description());
                        headers.add(header);
                    }
                }
                restAPIForm.setHeaders(headers);

                List<Map<String, String>> errors = new ArrayList<Map<String, String>>();
                if (apiMethod.errors() != null && apiMethod.errors().length > 0) {
                    for (ApiError apiError : apiMethod.errors()) {
                        Map<String, String> error = new HashMap<String, String>();
                        error.put("code", String.valueOf(apiError.code()));
                        error.put("description", apiError.description());
                        errors.add(error);
                    }
                }
                restAPIForm.setErrors(errors);

                if (apiMethod.requestObject() != Null.class && apiMethod.requestObject() != Void.class) {
                    if (apiMethod.requestObject().isAnnotationPresent(ApiObject.class)) {
                        queueForm.push(apiMethod.requestObject());
                        restAPIForm.setRequestObject(apiMethod.requestObject().getName());
                    } else {
                        restAPIForm.setRequestObject(apiMethod.requestObject().getSimpleName());
                    }
                } else {
                    restAPIForm.setRequestObject("");
                }

                if (apiMethod.responseObject() != Null.class) {
                    if (apiMethod.responseObject() != Void.class) {
                        if (apiMethod.requestObject().isAnnotationPresent(ApiObject.class)) {
                            queueForm.push(apiMethod.requestObject());
                            restAPIForm.setResponseObject(apiMethod.responseObject().getName());
                        } else {
                            restAPIForm.setResponseObject(apiMethod.responseObject().getSimpleName());
                        }
                    } else {
                        restAPIForm.setResponseObject("null");
                    }
                }

                restAPIForm.setResponseDescription(apiMethod.responseDescription());

                restAPIForms.add(restAPIForm);
            }
        }

        List<Class<?>> objectForms = new ArrayList<Class<?>>();
        while (!queueForm.isEmpty()) {
            Class<?> clazz = queueForm.pop();
            PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(clazz);
            if (propertyDescriptors != null && propertyDescriptors.length > 0) {
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    if (!objectForms.contains(clazz)) {
                        if (propertyDescriptor.getPropertyType() == List.class) {
                            try {
                                Field field = clazz.getDeclaredField(propertyDescriptor.getName());
                                ParameterizedType type = (ParameterizedType) field.getGenericType();
                                Class<?> cla = (Class<?>) type.getActualTypeArguments()[0];
                                if (cla.isAnnotationPresent(ApiObject.class)) {
                                    queueForm.push(cla);
                                }
                            } catch (NoSuchFieldException e) {
                            }
                        } else if (propertyDescriptor.getPropertyType().isArray()) {
                            try {
                                Field field = clazz.getDeclaredField(propertyDescriptor.getName());
                                Class<?> cla = ((Class<?>) field.getType()).getComponentType();
                                if (cla.isAnnotationPresent(ApiObject.class)) {
                                    queueForm.push(cla);
                                }
                            } catch (NoSuchFieldException e) {
                            }
                        } else {
                            if (propertyDescriptor.getPropertyType().isAnnotationPresent(ApiObject.class)) {
                                queueForm.push(propertyDescriptor.getPropertyType());
                            }
                        }
                    }
                }
            }
            if (!objectForms.contains(clazz)) {
                objectForms.add(clazz);
            }
        }

        for (Class<?> objectForm : objectForms) {
            ObjectAPIForm objectAPIForm = new ObjectAPIForm();
            ApiObject apiObject = objectForm.getAnnotation(ApiObject.class);
            objectAPIForm.setDeprecated(objectForm.isAnnotationPresent(Deprecated.class));
            objectAPIForm.setName(objectForm.getName());
            objectAPIForm.setDescription(apiObject.description());
            List<List<Map<String, String>>> fields = new ArrayList<List<Map<String, String>>>();
            PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(objectForm);
            if (propertyDescriptors != null && propertyDescriptors.length > 0) {
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    if (propertyDescriptor.getName().equals("class")) {
                        continue;
                    }
                    List<Map<String, String>> field = new ArrayList<Map<String, String>>();

                    ApiObjectField apiObjectField = null;
                    Field reflectionField = null;
                    try {
                        reflectionField = objectForm.getDeclaredField(propertyDescriptor.getName());
                    } catch (NoSuchFieldException e) {
                    }
                    if (reflectionField != null) {
                        if (reflectionField.isAnnotationPresent(ApiObjectField.class)) {
                            apiObjectField = reflectionField.getAnnotation(ApiObjectField.class);
                        }
                    }

                    String fieldType = "";
                    if (propertyDescriptor.getPropertyType() == List.class) {
                        try {
                            Field temp = objectForm.getDeclaredField(propertyDescriptor.getName());
                            ParameterizedType type = (ParameterizedType) temp.getGenericType();
                            Class<?> cla = (Class<?>) type.getActualTypeArguments()[0];
                            if (cla.isAnnotationPresent(ApiObject.class)) {
                                fieldType = "Type: " + cla.getName() + "[]";
                            } else {
                                fieldType = "Type: " + cla.getSimpleName() + "[]";
                            }
                        } catch (NoSuchFieldException e) {
                        }
                    } else if (propertyDescriptor.getPropertyType().isArray()) {
                        try {
                            Field temp = objectForm.getDeclaredField(propertyDescriptor.getName());

                            Class<?> cla = ((Class<?>) temp.getType()).getComponentType();

                            if (cla.isAnnotationPresent(ApiObject.class)) {
                                fieldType = "Type: " + cla.getName() + "[]";
                            } else {
                                fieldType = "Type: " + cla.getSimpleName() + "[]";
                            }
                        } catch (NoSuchFieldException e) {
                        }
                    } else {
                        if (propertyDescriptor.getPropertyType().isAnnotationPresent(ApiObject.class)) {
                            fieldType = "Type: " + propertyDescriptor.getPropertyType().getName();
                        } else {
                            fieldType = "Type: " + propertyDescriptor.getPropertyType().getSimpleName();
                        }
                    }

                    if (apiObjectField != null && !"".equals(apiObjectField.description())) {
                        Map<String, String> name = new HashMap<String, String>();
                        name.put("name", propertyDescriptor.getName());
                        name.put("value", apiObjectField.description());
                        field.add(name);

                        Map<String, String> type = new HashMap<String, String>();
                        type.put("name", "");
                        type.put("value", "Type: " + fieldType);
                        field.add(type);

                        if (apiObjectField.format() != null && !"".equals(apiObjectField.format())) {
                            Map<String, String> format = new HashMap<String, String>();
                            format.put("", "");
                            format.put("value", "Format: " + apiObjectField.format());
                            field.add(format);
                        }
                    } else {
                        Map<String, String> name = new HashMap<String, String>();
                        name.put("name", propertyDescriptor.getName());
                        name.put("value", "Type: " + fieldType);
                        field.add(name);
                    }

                    fields.add(field);
                }
            }
            objectAPIForm.setFields(fields);
            objectAPIForms.add(objectAPIForm);
        }
    }
}
