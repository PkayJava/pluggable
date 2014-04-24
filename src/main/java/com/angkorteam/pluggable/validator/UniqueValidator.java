package com.angkorteam.pluggable.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.util.lang.Classes;
import org.apache.wicket.validation.ValidationError;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.angkorteam.pluggable.core.AbstractWebApplication;
import com.angkorteam.pluggable.utilities.TableUtilities;
import com.angkorteam.pluggable.validation.constraints.Unique;

/**
 * @author Socheat KHAUV
 */
public class UniqueValidator implements IFormValidator {

    /**
     * 
     */
    private static final long serialVersionUID = 7260886875708554728L;

    private Unique unique;

    private Map<String, Object> model;

    private Map<String, Object> components;

    private FormComponent<?> component;

    public UniqueValidator(Unique query, Map<String, Object> model, FormComponent<?> component, Map<String, Object> components) {
        this.unique = query;
        this.model = model;
        this.component = component;
        this.components = components;
    }

    public UniqueValidator(Unique query) {
        this.unique = query;
    }

    @Override
    public FormComponent<?>[] getDependentFormComponents() {

        List<FormComponent<?>> deps = new ArrayList<FormComponent<?>>();

        String where = unique.where();
        where = where.replace(" and ", "[||]");
        where = where.replace(" or ", "[||]");
        String[] criterias = StringUtils.split(where, "[||]");

        for (String criteria : criterias) {
            int b = criteria.indexOf(":");
            if (b > -1) {
                String tmp = criteria.substring(b + 1).trim();
                if (this.components.containsKey(tmp)) {
                    deps.add((FormComponent<?>) this.components.get(tmp));
                }
            }
        }

        return deps.toArray(new FormComponent<?>[deps.size()]);
    }

    @Override
    public void validate(Form<?> form) {

        AbstractWebApplication application = (AbstractWebApplication) form.getApplication();

        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();

        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(jdbcTemplate);

        Map<String, Object> params = new HashMap<String, Object>();

        String where = unique.where();
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
                    params.put(tmp, this.model.get(tmp));
                }
            }
        }

        String tableName = TableUtilities.getTableName(this.unique.entity());

        String query = "select count(*) from " + tableName + " where " + unique.where();
        long count = template.queryForObject(query, params, Long.class);

        if (count > 0) {
            ValidationError error = new ValidationError().addKey(Classes.simpleName(getClass()));
            error.setVariable("input", this.component.getValue());
            component.error(error);
        }
    }
}
