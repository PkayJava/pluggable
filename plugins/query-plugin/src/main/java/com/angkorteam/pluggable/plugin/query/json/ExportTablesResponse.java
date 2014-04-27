package com.angkorteam.pluggable.plugin.query.json;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.angkorteam.pluggable.framework.doc.ApiObject;
import com.angkorteam.pluggable.framework.json.HttpMessage;

@ApiObject
public class ExportTablesResponse extends HttpMessage<List<String>> {

    /**
     * 
     */
    private static final long serialVersionUID = -8543306980479829898L;

    public ExportTablesResponse(int code, String message, List<String> content) {
        super(code, message, content);
    }

//    public static void main(String[] args) {
//        // FieldUtils.getField(cls, fieldName)
//        Class<ExportTablesResponse> clazz = ExportTablesResponse.class;
//        ParameterizedType parameterizedType = (ParameterizedType) clazz
//                .getGenericSuperclass();
//        Type[] types = parameterizedType.getActualTypeArguments();
//        types[0];
//        System.out.println("");
//        for (Field field : FieldUtils.getAllFields(ExportTablesResponse.class)) {
//            System.out.println(field.getName());
//        }
//
//        System.out.println(FieldUtils.getField(ExportTablesResponse.class,
//                "content") == null);
//    }
}
