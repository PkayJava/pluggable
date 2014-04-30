package com.angkorteam.pluggable.framework.utilities;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
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
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.wicket.WicketRuntimeException;
import org.springframework.beans.BeanUtils;

import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.doc.ApiError;
import com.angkorteam.pluggable.framework.doc.ApiHeader;
import com.angkorteam.pluggable.framework.doc.ApiMethod;
import com.angkorteam.pluggable.framework.doc.ApiObject;
import com.angkorteam.pluggable.framework.doc.ApiObjectField;
import com.angkorteam.pluggable.framework.doc.ApiParam;
import com.angkorteam.pluggable.framework.json.ObjectAPIForm;
import com.angkorteam.pluggable.framework.json.RestAPIForm;
import com.angkorteam.pluggable.framework.rest.RequestMapping;
import com.angkorteam.pluggable.framework.wicket.RequestMappingInfo;
import com.angkorteam.pluggable.framework.wicket.RestController;
import com.angkorteam.pluggable.framework.wicket.authroles.Role;
import com.angkorteam.pluggable.framework.wicket.authroles.Secured;

/**
 * @author Socheat KHAUV
 */
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

    public static final void fillRestAPI(HttpServletRequest request,
            AbstractWebApplication application, List<RestAPIForm> restAPIForms,
            List<ObjectAPIForm> objectAPIForms) {

        Stack<Class<?>> queueForm = new Stack<Class<?>>();

        String contextPath = request.getContextPath();
        if (contextPath == null || "".equals(contextPath)
                || contextPath.equals("/")) {
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

        for (Entry<String, RequestMappingInfo> info : application
                .getControllers().entrySet()) {

            Method method = info.getValue().getMethod();

            if (method.isAnnotationPresent(RequestMapping.class)
                    && method.isAnnotationPresent(ApiMethod.class)) {
                RequestMapping requestMapping = method
                        .getAnnotation(RequestMapping.class);
                ApiMethod apiMethod = method.getAnnotation(ApiMethod.class);
                String path = requestMapping.value();
                if (path.endsWith("/")) {
                    path = path.substring(0, path.length() - 1);
                }
                path = contextPath + path.replace(":.*}", "}");
                RestAPIForm restAPIForm = new RestAPIForm();
                restAPIForm.setDeprecated(method
                        .isAnnotationPresent(Deprecated.class));
                restAPIForm.setPath(path);
                restAPIForm.setDescription(apiMethod.description());

                if (method.isAnnotationPresent(Secured.class)) {
                    Secured secured = method.getAnnotation(Secured.class);
                    List<String> roles = new ArrayList<String>();
                    for (Role role : secured.roles()) {
                        roles.add(role.name());
                    }
                    restAPIForm
                            .setRoles(roles.toArray(new String[roles.size()]));
                } else {
                    restAPIForm.setRoles(new String[] {});
                }

                restAPIForm.setMethod(requestMapping.method());

                List<List<Map<String, String>>> formParameters = new ArrayList<List<Map<String, String>>>();
                for (Annotation[] annons : method.getParameterAnnotations()) {
                    ApiParam apiParam = null;
                    for (Annotation annon : annons) {
                        if (annon.annotationType() == ApiParam.class) {
                            apiParam = (ApiParam) annon;
                            break;
                        }
                    }
                    if (apiParam != null) {
                        List<Map<String, String>> formParameter = new ArrayList<Map<String, String>>();
                        if (apiParam.description() != null
                                && !"".equals(apiParam.description())) {
                            Map<String, String> name = new HashMap<String, String>();
                            name.put("name", apiParam.name());
                            name.put("value", apiParam.description());
                            formParameter.add(name);

                            Map<String, String> type = new HashMap<String, String>();
                            type.put("name", "");
                            if (apiParam.type() == Null.class) {
                                type.put("value", "Type: String");
                            } else {
                                type.put("value", "Type: "
                                        + apiParam.type().getSimpleName());
                            }
                            formParameter.add(type);

                        } else {
                            Map<String, String> name = new HashMap<String, String>();
                            name.put("name", apiParam.name());
                            name.put("value", "Type: "
                                    + apiParam.type().getSimpleName());
                            formParameter.add(name);
                        }
                        Map<String, String> required = new HashMap<String, String>();
                        required.put("name", "");
                        required.put(
                                "value",
                                "Required: "
                                        + String.valueOf(apiParam.required()));
                        formParameter.add(required);

                        if (apiParam.allowedvalues() != null
                                && apiParam.allowedvalues().length > 0) {
                            Map<String, String> allowedvalues = new HashMap<String, String>();
                            allowedvalues.put("name", "");
                            allowedvalues.put(
                                    "value",
                                    "Allowed values: "
                                            + StringUtils.join(
                                                    apiParam.allowedvalues(),
                                                    ","));
                            formParameter.add(allowedvalues);
                        }
                        if (apiParam.format() != null
                                && !"".equals(apiParam.format())) {
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
                if (apiMethod.headers() != null
                        && apiMethod.headers().length > 0) {
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

                Class<?> returnObject = method.getReturnType();
                if (returnObject == null
                        || !returnObject
                                .getName()
                                .equals(com.angkorteam.pluggable.framework.rest.Result.class
                                        .getName())) {
                    throw new WicketRuntimeException(method.getName()
                            + " is not rest api compatible");
                }

                Type type = (ParameterizedType) method.getGenericReturnType();
                if (type == null) {
                    throw new WicketRuntimeException(method.getName()
                            + " is not rest api compatible");
                }
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> responseObject = (Class<?>) parameterizedType
                        .getActualTypeArguments()[0];

                if (responseObject != Null.class) {
                    if (responseObject != Void.class) {
                        if (responseObject.isAnnotationPresent(ApiObject.class)) {
                            queueForm.push(responseObject);
                            restAPIForm.setResponseObject(responseObject
                                    .getName());
                        } else {
                            restAPIForm.setResponseObject(responseObject
                                    .getSimpleName());
                        }
                    } else {
                        restAPIForm.setResponseObject("null");
                    }
                }

                restAPIForm.setResponseDescription(apiMethod
                        .responseDescription());

                restAPIForms.add(restAPIForm);
            }
        }

        List<Class<?>> objectForms = new ArrayList<Class<?>>();
        while (!queueForm.isEmpty()) {
            Class<?> clazz = queueForm.pop();
            PropertyDescriptor[] propertyDescriptors = BeanUtils
                    .getPropertyDescriptors(clazz);
            if (propertyDescriptors != null && propertyDescriptors.length > 0) {
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    if (!objectForms.contains(clazz)) {
                        if (propertyDescriptor.getPropertyType() == List.class) {
                            try {
                                FieldUtils.getDeclaredField(clazz,
                                        propertyDescriptor.getName());
                                Field field = clazz
                                        .getDeclaredField(propertyDescriptor
                                                .getName());
                                ParameterizedType type = (ParameterizedType) field
                                        .getGenericType();
                                Class<?> cla = (Class<?>) type
                                        .getActualTypeArguments()[0];
                                if (cla.isAnnotationPresent(ApiObject.class)) {
                                    queueForm.push(cla);
                                }
                            } catch (NoSuchFieldException e) {
                            }
                        } else if (propertyDescriptor.getPropertyType()
                                .isArray()) {
                            try {
                                Field field = clazz
                                        .getDeclaredField(propertyDescriptor
                                                .getName());
                                Class<?> cla = ((Class<?>) field.getType())
                                        .getComponentType();
                                if (cla.isAnnotationPresent(ApiObject.class)) {
                                    queueForm.push(cla);
                                }
                            } catch (NoSuchFieldException e) {
                            }
                        } else {
                            if (propertyDescriptor.getPropertyType()
                                    .isAnnotationPresent(ApiObject.class)) {
                                queueForm.push(propertyDescriptor
                                        .getPropertyType());
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
            objectAPIForm.setDeprecated(objectForm
                    .isAnnotationPresent(Deprecated.class));
            objectAPIForm.setName(objectForm.getName());
            objectAPIForm.setDescription(apiObject.description());
            List<List<Map<String, String>>> fields = new ArrayList<List<Map<String, String>>>();
            PropertyDescriptor[] propertyDescriptors = BeanUtils
                    .getPropertyDescriptors(objectForm);
            if (propertyDescriptors != null && propertyDescriptors.length > 0) {
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    if (propertyDescriptor.getName().equals("class")) {
                        continue;
                    }
                    List<Map<String, String>> field = new ArrayList<Map<String, String>>();

                    ApiObjectField apiObjectField = null;
                    Field reflectionField = null;
                    try {
                        reflectionField = objectForm
                                .getDeclaredField(propertyDescriptor.getName());
                    } catch (NoSuchFieldException e) {
                    }
                    if (reflectionField != null) {
                        if (reflectionField
                                .isAnnotationPresent(ApiObjectField.class)) {
                            apiObjectField = reflectionField
                                    .getAnnotation(ApiObjectField.class);
                        }
                    }

                    String fieldType = "";
                    if (propertyDescriptor.getPropertyType() == List.class) {
                        try {
                            ParameterizedType type = null;
                            if (!"content".equals(propertyDescriptor.getName())) {
                                Field temp = objectForm
                                        .getDeclaredField(propertyDescriptor
                                                .getName());
                                type = (ParameterizedType) temp
                                        .getGenericType();
                            } else {
                                ParameterizedType parameterizedType = (ParameterizedType) objectForm
                                        .getGenericSuperclass();
                                Type[] types = parameterizedType
                                        .getActualTypeArguments();
                                type = (ParameterizedType) types[0];
                            }
                            Type type1 = type.getActualTypeArguments()[0];
                            if (type1 instanceof ParameterizedType) {
                                ParameterizedType pp = (ParameterizedType) type1;
                                fieldType = "Type : "
                                        + ((Class) pp.getRawType())
                                                .getSimpleName()
                                        + "<"
                                        + ((Class) pp.getActualTypeArguments()[0])
                                                .getSimpleName()
                                        + ","
                                        + ((Class) pp.getActualTypeArguments()[1])
                                                .getSimpleName() + ">" + "[]";
                            } else {
                                Class<?> cla = (Class<?>) type
                                        .getActualTypeArguments()[0];
                                if (cla.isAnnotationPresent(ApiObject.class)) {
                                    fieldType = "Type : " + cla.getName()
                                            + "[]";
                                } else {
                                    fieldType = "Type : " + cla.getSimpleName()
                                            + "[]";
                                }
                            }
                        } catch (NoSuchFieldException e) {
                        }
                    } else if (propertyDescriptor.getPropertyType().isArray()) {
                        try {
                            Field temp = objectForm
                                    .getDeclaredField(propertyDescriptor
                                            .getName());

                            Class<?> cla = ((Class<?>) temp.getType())
                                    .getComponentType();

                            if (cla.isAnnotationPresent(ApiObject.class)) {
                                fieldType = "Type : " + cla.getName() + "[]";
                            } else {
                                fieldType = "Type : " + cla.getSimpleName()
                                        + "[]";
                            }
                        } catch (NoSuchFieldException e) {
                        }
                    } else {
                        if (propertyDescriptor.getPropertyType()
                                .isAnnotationPresent(ApiObject.class)) {
                            fieldType = "Type : "
                                    + propertyDescriptor.getPropertyType()
                                            .getName();
                        } else {
                            fieldType = "Type : "
                                    + propertyDescriptor.getPropertyType()
                                            .getSimpleName();
                        }
                    }

                    if (apiObjectField != null
                            && !"".equals(apiObjectField.description())) {
                        Map<String, String> name = new HashMap<String, String>();
                        name.put("name", propertyDescriptor.getName());
                        name.put("value", apiObjectField.description());
                        field.add(name);

                        Map<String, String> type = new HashMap<String, String>();
                        type.put("name", "");
                        type.put("value", " " + fieldType);
                        field.add(type);

                        if (apiObjectField.format() != null
                                && !"".equals(apiObjectField.format())) {
                            Map<String, String> format = new HashMap<String, String>();
                            format.put("", "");
                            format.put("value",
                                    "Format: " + apiObjectField.format());
                            field.add(format);
                        }
                    } else {
                        Map<String, String> name = new HashMap<String, String>();
                        name.put("name", propertyDescriptor.getName());
                        name.put("value", " " + fieldType);
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
