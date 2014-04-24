package com.angkorteam.pluggable.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.form.FormComponent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.angkorteam.pluggable.database.MapRowMapper;
import com.angkorteam.pluggable.validation.type.Choice;

/**
 * @author Socheat KHAUV
 */
public class SelectionUtilities {

    private SelectionUtilities() {
    }

    public static final String[] toStringArray(Long[] value) {
        String[] result = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            result[i] = String.valueOf(value[i]);
        }
        return result;
    }

    public static final String[] toStringArray(long[] value) {
        String[] result = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            result[i] = String.valueOf(value[i]);
        }
        return result;
    }

    public static final String[] toStringArray(float[] value) {
        String[] result = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            result[i] = String.valueOf(value[i]);
        }
        return result;
    }

    public static final String[] toStringArray(Float[] value) {
        String[] result = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            result[i] = String.valueOf(value[i]);
        }
        return result;
    }

    public static final String[] toStringArray(byte[] value) {
        String[] result = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            result[i] = String.valueOf(value[i]);
        }
        return result;
    }

    public static final String[] toStringArray(Byte[] value) {
        String[] result = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            result[i] = String.valueOf(value[i]);
        }
        return result;
    }

    public static final String[] toStringArray(Short[] value) {
        String[] result = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            result[i] = String.valueOf(value[i]);
        }
        return result;
    }

    public static final String[] toStringArray(short[] value) {
        String[] result = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            result[i] = String.valueOf(value[i]);
        }
        return result;
    }

    public static final String[] toStringArray(double[] value) {
        String[] result = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            result[i] = String.valueOf(value[i]);
        }
        return result;
    }

    public static final String[] toStringArray(Double[] value) {
        String[] result = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            result[i] = String.valueOf(value[i]);
        }
        return result;
    }

    public static final String[] toStringArray(Integer[] value) {
        String[] result = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            result[i] = String.valueOf(value[i]);
        }
        return result;
    }

    public static final String[] toStringArray(int[] value) {
        String[] result = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            result[i] = String.valueOf(value[i]);
        }
        return result;
    }

    public static final List<Map<String, String>> getSystemQueryChoices(JdbcTemplate jdbcTemplate, Class<?> clazz, String display, String query, Map<String, Object> model, Map<String, Object> components) {
        String valueField = TableUtilities.getIdentityField(clazz);

        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);

        Map<String, Object> params = new HashMap<String, Object>();

        String where = query;
        where = where.replace(" and ", "[||]");
        where = where.replace(" or ", "[||]");
        String[] criterias = StringUtils.split(where, "[||]");

        for (String criteria : criterias) {
            int b = criteria.indexOf(":");
            if (b > -1) {
                String tmp = criteria.substring(b + 1).trim();
                if (components.containsKey(tmp)) {
                    FormComponent<?> component = (FormComponent<?>) components.get(tmp);
                    params.put(tmp, component.getInput());
                } else {
                    params.put(tmp, model.get(tmp));
                }
            }
        }

        String whereQuery = "";
        if (query != null && !"".equals(query)) {
            whereQuery = " where " + query;
        }

        return template.query("select " + valueField + " value, " + display + " display from " + TableUtilities.getTableName(clazz) + whereQuery, params, new MapRowMapper());
    }

    public static final List<Map<String, String>> getSystemJavaChoices(Choice[] choices) {
        List<Map<String, String>> values = new ArrayList<Map<String, String>>();
        for (Choice tmp : choices) {
            Map<String, String> tmp1 = new HashMap<String, String>();
            tmp1.put("display", tmp.display());
            tmp1.put("value", tmp.value());
            values.add(tmp1);
        }
        return values;
    }

    public static final Map<String, String> getUserQueryChoice(JdbcTemplate jdbcTemplate, Object object, String display) {
        Class<?> clazz = object.getClass();
        String id = TableUtilities.getIdentityField(clazz);
        String value = TableUtilities.getIdentityValue(object);
        return jdbcTemplate.queryForObject("select " + id + " value, " + display + " display from " + TableUtilities.getTableName(clazz) + " where " + id + " = ?", new MapRowMapper(), value);
    }

    public static final List<Map<String, String>> getUserQueryChoices(JdbcTemplate jdbcTemplate, Object[] objects, String display) {
        Class<?> clazz = objects.getClass().getComponentType();
        String id = TableUtilities.getIdentityField(clazz);
        List<Map<String, String>> results = new ArrayList<Map<String, String>>();
        for (Object object : objects) {
            String value = TableUtilities.getIdentityValue(object);
            Map<String, String> result = jdbcTemplate.queryForObject("select " + id + " value, " + display + " display from " + TableUtilities.getTableName(clazz) + " where " + id + " = ?", new MapRowMapper(), value);
            results.add(result);
        }
        return results;
    }

    public static final List<Map<String, String>> getUserJavaChoices(long[] userValues, Choice[] choices) {
        String[] values = toStringArray(userValues);
        return getUserJavaChoices(values, choices);
    }

    public static final List<Map<String, String>> getUserJavaChoices(Long[] userValues, Choice[] choices) {
        String[] values = toStringArray(userValues);
        return getUserJavaChoices(values, choices);
    }

    public static final List<Map<String, String>> getUserJavaChoices(Byte[] userValues, Choice[] choices) {
        String[] values = toStringArray(userValues);
        return getUserJavaChoices(values, choices);
    }

    public static final List<Map<String, String>> getUserJavaChoices(byte[] userValues, Choice[] choices) {
        String[] values = toStringArray(userValues);
        return getUserJavaChoices(values, choices);
    }

    public static final List<Map<String, String>> getUserJavaChoices(Short[] userValues, Choice[] choices) {
        String[] values = toStringArray(userValues);
        return getUserJavaChoices(values, choices);
    }

    public static final List<Map<String, String>> getUserJavaChoices(short[] userValues, Choice[] choices) {
        String[] values = toStringArray(userValues);
        return getUserJavaChoices(values, choices);
    }

    public static final List<Map<String, String>> getUserJavaChoices(Float[] userValues, Choice[] choices) {
        String[] values = toStringArray(userValues);
        return getUserJavaChoices(values, choices);
    }

    public static final List<Map<String, String>> getUserJavaChoices(float[] userValues, Choice[] choices) {
        String[] values = toStringArray(userValues);
        return getUserJavaChoices(values, choices);
    }

    public static final List<Map<String, String>> getUserJavaChoices(Double[] userValues, Choice[] choices) {
        String[] values = toStringArray(userValues);
        return getUserJavaChoices(values, choices);
    }

    public static final List<Map<String, String>> getUserJavaChoices(double[] userValues, Choice[] choices) {
        String[] values = toStringArray(userValues);
        return getUserJavaChoices(values, choices);
    }

    public static final List<Map<String, String>> getUserJavaChoices(Integer[] userValues, Choice[] choices) {
        String[] values = toStringArray(userValues);
        return getUserJavaChoices(values, choices);
    }

    public static final List<Map<String, String>> getUserJavaChoices(int[] userValues, Choice[] choices) {
        String[] values = toStringArray(userValues);
        return getUserJavaChoices(values, choices);
    }

    public static final Map<String, String> getUserJavaChoice(Long userValue, Choice[] choices) {
        return getUserJavaChoice(String.valueOf(userValue), choices);
    }

    public static final Map<String, String> getUserJavaChoice(Short userValue, Choice[] choices) {
        return getUserJavaChoice(String.valueOf(userValue), choices);
    }

    public static final Map<String, String> getUserJavaChoice(Byte userValue, Choice[] choices) {
        return getUserJavaChoice(String.valueOf(userValue), choices);
    }

    public static final Map<String, String> getUserJavaChoice(Integer userValue, Choice[] choices) {
        return getUserJavaChoice(String.valueOf(userValue), choices);
    }

    public static final Map<String, String> getUserJavaChoice(Float userValue, Choice[] choices) {
        return getUserJavaChoice(String.valueOf(userValue), choices);
    }

    public static final Map<String, String> getUserJavaChoice(Double userValue, Choice[] choices) {
        return getUserJavaChoice(String.valueOf(userValue), choices);
    }

    public static final Map<String, String> getUserJavaChoice(String userValue, Choice[] choices) {
        Map<String, String> tmps = new HashMap<String, String>();
        for (Choice choice : choices) {
            tmps.put(choice.value(), choice.display());
        }
        Map<String, String> tmp1 = new HashMap<String, String>();
        tmp1.put("display", tmps.get(userValue));
        tmp1.put("value", userValue);
        return tmp1;
    }

    public static final List<Map<String, String>> getUserJavaChoices(String[] userValues, Choice[] choices) {
        Map<String, String> tmps = new HashMap<String, String>();
        for (Choice choice : choices) {
            tmps.put(choice.value(), choice.display());
        }
        List<Map<String, String>> values = new ArrayList<Map<String, String>>();
        for (String tmp : userValues) {
            Map<String, String> tmp1 = new HashMap<String, String>();
            tmp1.put("display", tmps.get(tmp));
            tmp1.put("value", tmp);
            values.add(tmp1);
        }
        return values;
    }
}
