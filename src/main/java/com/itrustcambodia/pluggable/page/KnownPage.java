package com.itrustcambodia.pluggable.page;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.reflections.ReflectionUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.layout.AbstractLayout;
import com.itrustcambodia.pluggable.panel.widget.WidgetCheckBox;
import com.itrustcambodia.pluggable.panel.widget.WidgetCheckBoxMultipleChoice;
import com.itrustcambodia.pluggable.panel.widget.WidgetDateField;
import com.itrustcambodia.pluggable.panel.widget.WidgetDateTimeField;
import com.itrustcambodia.pluggable.panel.widget.WidgetDropDownChoice;
import com.itrustcambodia.pluggable.panel.widget.WidgetFileUploadField;
import com.itrustcambodia.pluggable.panel.widget.WidgetLabelField;
import com.itrustcambodia.pluggable.panel.widget.WidgetListMultipleChoice;
import com.itrustcambodia.pluggable.panel.widget.WidgetMultiFileUploadField;
import com.itrustcambodia.pluggable.panel.widget.WidgetPasswordTextField;
import com.itrustcambodia.pluggable.panel.widget.WidgetRadioChoice;
import com.itrustcambodia.pluggable.panel.widget.WidgetSelect2Choice;
import com.itrustcambodia.pluggable.panel.widget.WidgetSelect2MultiChoice;
import com.itrustcambodia.pluggable.panel.widget.WidgetTextArea;
import com.itrustcambodia.pluggable.panel.widget.WidgetTextField;
import com.itrustcambodia.pluggable.panel.widget.WidgetTimeField;
import com.itrustcambodia.pluggable.utilities.SelectionUtilities;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.validation.constraints.DecimalMax;
import com.itrustcambodia.pluggable.validation.constraints.DecimalMin;
import com.itrustcambodia.pluggable.validation.constraints.Digits;
import com.itrustcambodia.pluggable.validation.constraints.Future;
import com.itrustcambodia.pluggable.validation.constraints.Max;
import com.itrustcambodia.pluggable.validation.constraints.Min;
import com.itrustcambodia.pluggable.validation.constraints.NotNull;
import com.itrustcambodia.pluggable.validation.constraints.Past;
import com.itrustcambodia.pluggable.validation.constraints.Pattern;
import com.itrustcambodia.pluggable.validation.constraints.Size;
import com.itrustcambodia.pluggable.validation.constraints.Unique;
import com.itrustcambodia.pluggable.validation.controller.ButtonController;
import com.itrustcambodia.pluggable.validation.controller.ChoiceController;
import com.itrustcambodia.pluggable.validation.controller.FieldController;
import com.itrustcambodia.pluggable.validation.controller.Navigation;
import com.itrustcambodia.pluggable.validation.type.Choice;
import com.itrustcambodia.pluggable.validation.type.ChoiceType;
import com.itrustcambodia.pluggable.validation.type.TextFieldType;
import com.itrustcambodia.pluggable.widget.CheckBox;
import com.itrustcambodia.pluggable.widget.CheckBoxMultipleChoice;
import com.itrustcambodia.pluggable.widget.DropDownChoice;
import com.itrustcambodia.pluggable.widget.FileUploadField;
import com.itrustcambodia.pluggable.widget.LabelField;
import com.itrustcambodia.pluggable.widget.ListMultipleChoice;
import com.itrustcambodia.pluggable.widget.MultiFileUploadField;
import com.itrustcambodia.pluggable.widget.RadioChoice;
import com.itrustcambodia.pluggable.widget.Select2Choice;
import com.itrustcambodia.pluggable.widget.Select2MultiChoice;
import com.itrustcambodia.pluggable.widget.TextArea;
import com.itrustcambodia.pluggable.widget.TextField;
import com.vaynberg.wicket.select2.ChoiceProvider;
import com.vaynberg.wicket.select2.DragAndDropBehavior;

/**
 * @author Socheat KHAUV
 */
public abstract class KnownPage extends WebPage {

    /**
     * 
     */
    private static final long serialVersionUID = 4257287705775101884L;

    private Map<String, Object> model = new HashMap<String, Object>();

    private Map<String, ButtonController> buttonController = new HashMap<String, ButtonController>();

    private Map<String, FieldController> fieldController = new HashMap<String, FieldController>();

    private Map<String, Class<?>> fieldTypes = new HashMap<String, Class<?>>();

    private Map<String, Object> components = new HashMap<String, Object>();

    private Map<String, FormComponent<?>> buttons = new HashMap<String, FormComponent<?>>();

    private Form<Void> form;

    public KnownPage() {
        super();
        initializeInterceptor();
    }

    public Map<String, Object> getModel() {
        return model;
    }

    protected String getFormTitle() {
        return "";
    }

    public KnownPage(IModel<?> model) {
        super(model);
        initializeInterceptor();
    }

    protected Object getFormComponent(String name) {
        return this.components.get(name);
    }

    protected Form<?> getForm() {
        return this.form;
    }

    protected FormComponent<?> getFormButton(String name) {
        return this.buttons.get(name);
    }

    public KnownPage(PageParameters parameters) {
        super(parameters);
        initializeInterceptor();
    }

    private void prepareModel(String query) {
        String where = query.replace(" and ", "[||]");
        where = where.replace(" or ", "[||]");
        String[] criterias = StringUtils.split(where, "[||]");

        for (String criteria : criterias) {
            int b = criteria.indexOf(":");
            if (b > -1) {
                String tmp = criteria.substring(b + 1).trim();
                try {
                    this.model.put(tmp, FieldUtils.readField(this, tmp, true));
                } catch (IllegalArgumentException e) {
                } catch (IllegalAccessException e) {
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onInitialize() {
        super.onInitialize();

        for (Field field : ReflectionUtils.getAllFields(this.getClass())) {

            if (field.getAnnotation(Unique.class) != null) {
                Unique unique = field.getAnnotation(Unique.class);
                String where = unique.where();
                if (where != null && !"".equals(where)) {
                    prepareModel(where);
                }
            }

            if (field.getAnnotation(TextField.class) != null) {
                TextField textField = field.getAnnotation(TextField.class);
                try {
                    Object value = FieldUtils.readField(field, this, true);
                    if (value != null) {
                        if (value instanceof String) {
                            model.put(field.getName(), value);
                        } else if (value instanceof Date) {
                            SimpleDateFormat format = new SimpleDateFormat(textField.pattern());
                            model.put(field.getName(), format.format((Date) value));
                        } else if (value instanceof Integer) {
                            model.put(field.getName(), String.valueOf((Integer) value));
                        } else if (value instanceof Byte) {
                            model.put(field.getName(), String.valueOf((Byte) value));
                        } else if (value instanceof Long) {
                            model.put(field.getName(), String.valueOf((Long) value));
                        } else if (value instanceof Short) {
                            model.put(field.getName(), String.valueOf((Short) value));
                        } else if (value instanceof Double) {
                            model.put(field.getName(), String.valueOf((Double) value));
                        } else if (value instanceof Float) {
                            model.put(field.getName(), String.valueOf((Float) value));
                        }
                    }
                } catch (IllegalAccessException e) {
                }
            } else if (field.getAnnotation(CheckBox.class) != null) {
                try {
                    Object value = FieldUtils.readField(field, this, true);
                    if (value != null) {
                        if (value instanceof String) {
                            model.put(field.getName(), Boolean.valueOf((String) value));
                        } else if (value instanceof Boolean) {
                            model.put(field.getName(), value);
                        }
                    }
                } catch (IllegalAccessException e) {
                }
            } else if (field.getAnnotation(LabelField.class) != null) {
                try {
                    model.put(field.getName(), FieldUtils.readField(field, this, true));
                } catch (IllegalAccessException e) {
                }
            } else if (field.getAnnotation(CheckBoxMultipleChoice.class) != null) {
                CheckBoxMultipleChoice checkBoxMultipleChoice = field.getAnnotation(CheckBoxMultipleChoice.class);
                if (checkBoxMultipleChoice.where() != null && !"".equals(checkBoxMultipleChoice.where())) {
                    prepareModel(checkBoxMultipleChoice.where());
                }

                if (checkBoxMultipleChoice.type() == ChoiceType.QUERY) {
                    org.apache.wicket.markup.html.form.CheckBoxMultipleChoice<Map<String, String>> component = (org.apache.wicket.markup.html.form.CheckBoxMultipleChoice<Map<String, String>>) this.components.get(field.getName());
                    List<Map<String, String>> choices = (List<Map<String, String>>) component.getChoices();

                    AbstractWebApplication application = (AbstractWebApplication) getApplication();
                    JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
                    List<Map<String, String>> values = SelectionUtilities.getSystemQueryChoices(jdbcTemplate, field.getType().getComponentType(), checkBoxMultipleChoice.display(), checkBoxMultipleChoice.where(), model, components);
                    if (values != null && !values.isEmpty()) {
                        for (Map<String, String> item : values) {
                            choices.add(item);
                        }
                    }
                }

                try {
                    Object object = FieldUtils.readField(field, this, true);
                    if (object != null) {
                        List<Map<String, String>> values = null;
                        if (checkBoxMultipleChoice.type() == ChoiceType.JAVA) {
                            if (object instanceof String[]) {
                                values = SelectionUtilities.getUserJavaChoices((String[]) object, checkBoxMultipleChoice.choices());
                            } else if (object instanceof Long[]) {
                                values = SelectionUtilities.getUserJavaChoices((Long[]) object, checkBoxMultipleChoice.choices());
                            } else if (object instanceof long[]) {
                                values = SelectionUtilities.getUserJavaChoices((long[]) object, checkBoxMultipleChoice.choices());
                            } else if (object instanceof Byte[]) {
                                values = SelectionUtilities.getUserJavaChoices((Byte[]) object, checkBoxMultipleChoice.choices());
                            } else if (object instanceof byte[]) {
                                values = SelectionUtilities.getUserJavaChoices((byte[]) object, checkBoxMultipleChoice.choices());
                            } else if (object instanceof Double[]) {
                                values = SelectionUtilities.getUserJavaChoices((Double[]) object, checkBoxMultipleChoice.choices());
                            } else if (object instanceof double[]) {
                                values = SelectionUtilities.getUserJavaChoices((double[]) object, checkBoxMultipleChoice.choices());
                            } else if (object instanceof Short[]) {
                                values = SelectionUtilities.getUserJavaChoices((Short[]) object, checkBoxMultipleChoice.choices());
                            } else if (object instanceof short[]) {
                                values = SelectionUtilities.getUserJavaChoices((short[]) object, checkBoxMultipleChoice.choices());
                            } else if (object instanceof Integer[]) {
                                values = SelectionUtilities.getUserJavaChoices((Integer[]) object, checkBoxMultipleChoice.choices());
                            } else if (object instanceof int[]) {
                                values = SelectionUtilities.getUserJavaChoices((int[]) object, checkBoxMultipleChoice.choices());
                            } else if (object instanceof Float[]) {
                                values = SelectionUtilities.getUserJavaChoices((Float[]) object, checkBoxMultipleChoice.choices());
                            } else if (object instanceof float[]) {
                                values = SelectionUtilities.getUserJavaChoices((float[]) object, checkBoxMultipleChoice.choices());
                            }
                        } else if (checkBoxMultipleChoice.type() == ChoiceType.QUERY) {
                            AbstractWebApplication application = (AbstractWebApplication) getApplication();
                            JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
                            values = SelectionUtilities.getUserQueryChoices(jdbcTemplate, (Object[]) object, checkBoxMultipleChoice.display());
                        }
                        model.put(field.getName(), values);
                    }
                } catch (IllegalAccessException e) {
                }
            } else if (field.getAnnotation(ListMultipleChoice.class) != null) {
                ListMultipleChoice listMultipleChoice = field.getAnnotation(ListMultipleChoice.class);
                if (listMultipleChoice.where() != null && !"".equals(listMultipleChoice.where())) {
                    prepareModel(listMultipleChoice.where());
                }

                if (listMultipleChoice.type() == ChoiceType.QUERY) {
                    org.apache.wicket.markup.html.form.ListMultipleChoice<Map<String, String>> component = (org.apache.wicket.markup.html.form.ListMultipleChoice<Map<String, String>>) this.components.get(field.getName());
                    List<Map<String, String>> choices = (List<Map<String, String>>) component.getChoices();

                    AbstractWebApplication application = (AbstractWebApplication) getApplication();
                    JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
                    List<Map<String, String>> values = SelectionUtilities.getSystemQueryChoices(jdbcTemplate, field.getType().getComponentType(), listMultipleChoice.display(), listMultipleChoice.where(), model, components);
                    if (values != null && !values.isEmpty()) {
                        for (Map<String, String> item : values) {
                            choices.add(item);
                        }
                    }
                }

                try {
                    Object object = FieldUtils.readField(field, this, true);
                    if (object != null) {
                        List<Map<String, String>> values = null;
                        if (listMultipleChoice.type() == ChoiceType.JAVA) {
                            if (object instanceof String[]) {
                                values = SelectionUtilities.getUserJavaChoices((String[]) object, listMultipleChoice.choices());
                            } else if (object instanceof Long[]) {
                                values = SelectionUtilities.getUserJavaChoices((Long[]) object, listMultipleChoice.choices());
                            } else if (object instanceof long[]) {
                                values = SelectionUtilities.getUserJavaChoices((long[]) object, listMultipleChoice.choices());
                            } else if (object instanceof Byte[]) {
                                values = SelectionUtilities.getUserJavaChoices((Byte[]) object, listMultipleChoice.choices());
                            } else if (object instanceof byte[]) {
                                values = SelectionUtilities.getUserJavaChoices((byte[]) object, listMultipleChoice.choices());
                            } else if (object instanceof Double[]) {
                                values = SelectionUtilities.getUserJavaChoices((Double[]) object, listMultipleChoice.choices());
                            } else if (object instanceof double[]) {
                                values = SelectionUtilities.getUserJavaChoices((double[]) object, listMultipleChoice.choices());
                            } else if (object instanceof Short[]) {
                                values = SelectionUtilities.getUserJavaChoices((Short[]) object, listMultipleChoice.choices());
                            } else if (object instanceof short[]) {
                                values = SelectionUtilities.getUserJavaChoices((short[]) object, listMultipleChoice.choices());
                            } else if (object instanceof Integer[]) {
                                values = SelectionUtilities.getUserJavaChoices((Integer[]) object, listMultipleChoice.choices());
                            } else if (object instanceof int[]) {
                                values = SelectionUtilities.getUserJavaChoices((int[]) object, listMultipleChoice.choices());
                            } else if (object instanceof Float[]) {
                                values = SelectionUtilities.getUserJavaChoices((Float[]) object, listMultipleChoice.choices());
                            } else if (object instanceof float[]) {
                                values = SelectionUtilities.getUserJavaChoices((float[]) object, listMultipleChoice.choices());
                            }
                        } else if (listMultipleChoice.type() == ChoiceType.QUERY) {
                            AbstractWebApplication application = (AbstractWebApplication) getApplication();
                            JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
                            values = SelectionUtilities.getUserQueryChoices(jdbcTemplate, (Object[]) object, listMultipleChoice.display());
                        }
                        model.put(field.getName(), values);
                    }
                } catch (IllegalAccessException e) {
                }
            } else if (field.getAnnotation(RadioChoice.class) != null) {
                RadioChoice radioChoice = field.getAnnotation(RadioChoice.class);
                if (radioChoice.where() != null && !"".equals(radioChoice.where())) {
                    prepareModel(radioChoice.where());
                }

                if (radioChoice.type() == ChoiceType.QUERY) {
                    org.apache.wicket.markup.html.form.RadioChoice<Map<String, String>> component = (org.apache.wicket.markup.html.form.RadioChoice<Map<String, String>>) this.components.get(field.getName());
                    List<Map<String, String>> choices = (List<Map<String, String>>) component.getChoices();

                    AbstractWebApplication application = (AbstractWebApplication) getApplication();
                    JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
                    List<Map<String, String>> values = SelectionUtilities.getSystemQueryChoices(jdbcTemplate, field.getType(), radioChoice.display(), radioChoice.where(), model, components);
                    if (values != null && !values.isEmpty()) {
                        for (Map<String, String> item : values) {
                            choices.add(item);
                        }
                    }
                }

                try {
                    Object value = FieldUtils.readField(field, this, true);
                    if (value != null) {
                        Map<String, String> values = null;
                        if (radioChoice.type() == ChoiceType.JAVA) {
                            if (value instanceof String) {
                                values = SelectionUtilities.getUserJavaChoice((String) value, radioChoice.choices());
                            } else if (value instanceof Integer) {
                                values = SelectionUtilities.getUserJavaChoice((Integer) value, radioChoice.choices());
                            } else if (value instanceof Byte) {
                                values = SelectionUtilities.getUserJavaChoice((Byte) value, radioChoice.choices());
                            } else if (value instanceof Long) {
                                values = SelectionUtilities.getUserJavaChoice((Long) value, radioChoice.choices());
                            } else if (value instanceof Short) {
                                values = SelectionUtilities.getUserJavaChoice((Short) value, radioChoice.choices());
                            } else if (value instanceof Double) {
                                values = SelectionUtilities.getUserJavaChoice((Double) value, radioChoice.choices());
                            } else if (value instanceof Float) {
                                values = SelectionUtilities.getUserJavaChoice((Float) value, radioChoice.choices());
                            }
                        } else if (radioChoice.type() == ChoiceType.QUERY) {
                            AbstractWebApplication application = (AbstractWebApplication) getApplication();
                            JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
                            values = SelectionUtilities.getUserQueryChoice(jdbcTemplate, value, radioChoice.display());
                        }
                        model.put(field.getName(), values);
                    }
                } catch (IllegalAccessException e) {
                }
            } else if (field.getAnnotation(DropDownChoice.class) != null) {
                DropDownChoice dropDownChoice = field.getAnnotation(DropDownChoice.class);
                if (dropDownChoice.where() != null && !"".equals(dropDownChoice.where())) {
                    prepareModel(dropDownChoice.where());
                }
                if (dropDownChoice.type() == ChoiceType.QUERY) {
                    org.apache.wicket.markup.html.form.DropDownChoice<Map<String, String>> component = (org.apache.wicket.markup.html.form.DropDownChoice<Map<String, String>>) this.components.get(field.getName());
                    List<Map<String, String>> choices = (List<Map<String, String>>) component.getChoices();

                    AbstractWebApplication application = (AbstractWebApplication) getApplication();
                    JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
                    List<Map<String, String>> values = SelectionUtilities.getSystemQueryChoices(jdbcTemplate, field.getType(), dropDownChoice.display(), dropDownChoice.where(), model, components);
                    if (values != null && !values.isEmpty()) {
                        for (Map<String, String> item : values) {
                            choices.add(item);
                        }
                    }
                }
                try {
                    Object value = FieldUtils.readField(field, this, true);
                    if (value != null) {
                        Map<String, String> values = null;
                        if (dropDownChoice.type() == ChoiceType.JAVA) {
                            if (value instanceof String) {
                                values = SelectionUtilities.getUserJavaChoice((String) value, dropDownChoice.choices());
                            } else if (value instanceof Integer) {
                                values = SelectionUtilities.getUserJavaChoice((Integer) value, dropDownChoice.choices());
                            } else if (value instanceof Byte) {
                                values = SelectionUtilities.getUserJavaChoice((Byte) value, dropDownChoice.choices());
                            } else if (value instanceof Long) {
                                values = SelectionUtilities.getUserJavaChoice((Long) value, dropDownChoice.choices());
                            } else if (value instanceof Short) {
                                values = SelectionUtilities.getUserJavaChoice((Short) value, dropDownChoice.choices());
                            } else if (value instanceof Double) {
                                values = SelectionUtilities.getUserJavaChoice((Double) value, dropDownChoice.choices());
                            } else if (value instanceof Float) {
                                values = SelectionUtilities.getUserJavaChoice((Float) value, dropDownChoice.choices());
                            }
                        } else if (dropDownChoice.type() == ChoiceType.QUERY) {
                            AbstractWebApplication application = (AbstractWebApplication) getApplication();
                            JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
                            values = SelectionUtilities.getUserQueryChoice(jdbcTemplate, value, dropDownChoice.display());
                        }
                        model.put(field.getName(), values);
                    }
                } catch (IllegalAccessException e) {
                }
            } else if (field.getAnnotation(TextArea.class) != null) {
                try {
                    Object value = FieldUtils.readField(field, this, true);
                    if (value != null) {
                        if (value instanceof String) {
                            model.put(field.getName(), value);
                        }
                    }
                } catch (IllegalAccessException e) {
                }
            } else if (field.getAnnotation(Select2MultiChoice.class) != null) {
                try {
                    Object value = FieldUtils.readField(field, this, true);
                    if (value != null) {
                        Object[] values = (Object[]) value;
                        model.put(field.getName(), new ArrayList<Object>(Arrays.asList(values)));
                    }
                } catch (IllegalAccessException e) {
                }
            } else if (field.getAnnotation(Select2Choice.class) != null) {
                try {
                    Object value = FieldUtils.readField(field, this, true);
                    model.put(field.getName(), value);
                } catch (IllegalAccessException e) {
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeInterceptor() {
        AbstractLayout layout = requestLayout("layout");
        add(layout);

        Fragment fragment = null;

        if (getFormTitle() == null || "".equals(getFormTitle())) {
            fragment = new Fragment("fragment", "formFragment", this);
        } else {
            fragment = new Fragment("fragment", "labelFormFragment", this);
            Label formTitle = new Label("formTitle", getFormTitle());
            fragment.add(formTitle);
        }

        form = new Form<Void>("form");
        fragment.add(form);

        layout.add(fragment);

        List<FieldController> fields = new ArrayList<FieldController>();
        for (Field field : ReflectionUtils.getAllFields(this.getClass())) {
            FieldController widget = null;
            if (field.getAnnotation(TextField.class) != null) {
                widget = new FieldController();
                TextField textField = field.getAnnotation(TextField.class);
                widget.setTextField(textField);
                widget.setOrder(textField.order());

                if (textField.type() == TextFieldType.PASSWORD) {
                    org.apache.wicket.markup.html.form.TextField<String> component = new PasswordTextField("field", new PropertyModel<String>(model, field.getName()));
                    component.setType(String.class);
                    components.put(field.getName(), component);
                } else {
                    org.apache.wicket.markup.html.form.TextField<String> component = new org.apache.wicket.markup.html.form.TextField<String>("field", new PropertyModel<String>(model, field.getName()));
                    component.setType(String.class);
                    components.put(field.getName(), component);
                }
            } else if (field.getAnnotation(LabelField.class) != null) {
                widget = new FieldController();
                LabelField labelField = field.getAnnotation(LabelField.class);
                widget.setLabelField(labelField);
                widget.setOrder(labelField.order());

                Label component = new Label("field", "");
                components.put(field.getName(), component);
            } else if (field.getAnnotation(CheckBox.class) != null) {
                widget = new FieldController();
                CheckBox checkBox = field.getAnnotation(CheckBox.class);
                widget.setCheckBox(checkBox);
                widget.setOrder(checkBox.order());

                org.apache.wicket.markup.html.form.CheckBox component = new org.apache.wicket.markup.html.form.CheckBox("field", new PropertyModel<Boolean>(model, field.getName()));
                components.put(field.getName(), component);
            } else if (field.getAnnotation(CheckBoxMultipleChoice.class) != null) {
                widget = new FieldController();
                CheckBoxMultipleChoice checkBoxMultipleChoice = field.getAnnotation(CheckBoxMultipleChoice.class);
                widget.setCheckBoxMultipleChoice(checkBoxMultipleChoice);
                widget.setOrder(checkBoxMultipleChoice.order());

                List<Map<String, String>> values = null;
                if (widget.getCheckBoxMultipleChoice().type() == ChoiceType.JAVA) {
                    Choice[] choices = widget.getCheckBoxMultipleChoice().choices();
                    if (choices != null && choices.length > 0) {
                        values = SelectionUtilities.getSystemJavaChoices(widget.getCheckBoxMultipleChoice().choices());
                    }
                } else if (widget.getCheckBoxMultipleChoice().type() == ChoiceType.QUERY) {
                    values = new ArrayList<Map<String, String>>();
                }
                org.apache.wicket.markup.html.form.CheckBoxMultipleChoice<Map<String, String>> component = new org.apache.wicket.markup.html.form.CheckBoxMultipleChoice<Map<String, String>>("field", new PropertyModel<List<Map<String, String>>>(model, field.getName()), values, new ChoiceController());
                components.put(field.getName(), component);
            } else if (field.getAnnotation(ListMultipleChoice.class) != null) {
                widget = new FieldController();
                ListMultipleChoice listMultipleChoice = field.getAnnotation(ListMultipleChoice.class);
                widget.setListMultipleChoice(listMultipleChoice);
                widget.setOrder(listMultipleChoice.order());

                List<Map<String, String>> values = null;
                if (widget.getListMultipleChoice().type() == ChoiceType.JAVA) {
                    Choice[] choices = widget.getListMultipleChoice().choices();
                    if (choices != null && choices.length > 0) {
                        values = SelectionUtilities.getSystemJavaChoices(widget.getListMultipleChoice().choices());
                    }
                } else if (widget.getListMultipleChoice().type() == ChoiceType.QUERY) {
                    values = new ArrayList<Map<String, String>>();
                }
                org.apache.wicket.markup.html.form.ListMultipleChoice<Map<String, String>> component = new org.apache.wicket.markup.html.form.ListMultipleChoice<Map<String, String>>("field", new PropertyModel<List<Map<String, String>>>(model, field.getName()), values, new ChoiceController());
                components.put(field.getName(), component);
            } else if (field.getAnnotation(FileUploadField.class) != null) {
                widget = new FieldController();
                FileUploadField fileUploadField = field.getAnnotation(FileUploadField.class);
                widget.setFileUploadField(fileUploadField);
                widget.setOrder(fileUploadField.order());

                org.apache.wicket.markup.html.form.upload.FileUploadField component = new org.apache.wicket.markup.html.form.upload.FileUploadField("field", new PropertyModel<List<FileUpload>>(model, field.getName()));
                components.put(field.getName(), component);
            } else if (field.getAnnotation(MultiFileUploadField.class) != null) {
                widget = new FieldController();
                MultiFileUploadField multiFileUploadField = field.getAnnotation(MultiFileUploadField.class);
                widget.setMultiFileUploadField(multiFileUploadField);
                widget.setOrder(multiFileUploadField.order());

                model.put(field.getName(), new ArrayList<FileUpload>());
                org.apache.wicket.markup.html.form.upload.MultiFileUploadField component = new org.apache.wicket.markup.html.form.upload.MultiFileUploadField("field", new PropertyModel<List<FileUpload>>(model, field.getName()));
                components.put(field.getName(), component);
            } else if (field.getAnnotation(RadioChoice.class) != null) {
                widget = new FieldController();
                RadioChoice radioChoice = field.getAnnotation(RadioChoice.class);
                widget.setRadioChoice(radioChoice);
                widget.setOrder(radioChoice.order());

                List<Map<String, String>> values = null;
                if (widget.getRadioChoice().type() == ChoiceType.JAVA) {
                    Choice[] choices = widget.getRadioChoice().choices();
                    if (choices != null && choices.length > 0) {
                        values = SelectionUtilities.getSystemJavaChoices(widget.getRadioChoice().choices());
                    }
                } else if (widget.getRadioChoice().type() == ChoiceType.QUERY) {
                    values = new ArrayList<Map<String, String>>();
                }
                org.apache.wicket.markup.html.form.RadioChoice<Map<String, String>> component = new org.apache.wicket.markup.html.form.RadioChoice<Map<String, String>>("field", new PropertyModel<Map<String, String>>(model, field.getName()), values, new ChoiceController());
                components.put(field.getName(), component);
            } else if (field.getAnnotation(DropDownChoice.class) != null) {
                widget = new FieldController();
                DropDownChoice dropDownChoice = field.getAnnotation(DropDownChoice.class);
                widget.setDropDownChoice(dropDownChoice);
                widget.setOrder(dropDownChoice.order());

                List<Map<String, String>> values = null;
                if (widget.getDropDownChoice().type() == ChoiceType.JAVA) {
                    Choice[] choices = widget.getDropDownChoice().choices();
                    if (choices != null && choices.length > 0) {
                        values = SelectionUtilities.getSystemJavaChoices(widget.getDropDownChoice().choices());
                    }
                } else if (widget.getDropDownChoice().type() == ChoiceType.QUERY) {
                    values = new ArrayList<Map<String, String>>();
                }

                org.apache.wicket.markup.html.form.DropDownChoice<Map<String, String>> component = new org.apache.wicket.markup.html.form.DropDownChoice<Map<String, String>>("field", new PropertyModel<Map<String, String>>(model, field.getName()), values, new ChoiceController());
                components.put(field.getName(), component);
            } else if (field.getAnnotation(TextArea.class) != null) {
                widget = new FieldController();
                TextArea textArea = field.getAnnotation(TextArea.class);
                widget.setTextArea(textArea);
                widget.setOrder(textArea.order());

                org.apache.wicket.markup.html.form.TextArea<String> component = new org.apache.wicket.markup.html.form.TextArea<String>("field", new PropertyModel<String>(model, field.getName()));
                component.setType(String.class);
                components.put(field.getName(), component);
            } else if (field.getAnnotation(Select2MultiChoice.class) != null) {
                widget = new FieldController();
                Select2MultiChoice select2MultiChoice = field.getAnnotation(Select2MultiChoice.class);
                widget.setSelect2MultiChoice(select2MultiChoice);
                widget.setOrder(select2MultiChoice.order());

                ChoiceProvider<Serializable> provider = null;
                try {
                    provider = (ChoiceProvider<Serializable>) select2MultiChoice.provider().newInstance();
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                }

                com.vaynberg.wicket.select2.Select2MultiChoice<Serializable> component = new com.vaynberg.wicket.select2.Select2MultiChoice<Serializable>("field", new PropertyModel<Collection<Serializable>>(model, field.getName()), provider);
                component.setType(field.getType());
                component.getSettings().setMinimumInputLength(select2MultiChoice.minimumInputLength());
                component.add(new DragAndDropBehavior());
                components.put(field.getName(), component);
            } else if (field.getAnnotation(Select2Choice.class) != null) {
                widget = new FieldController();
                Select2Choice select2Choice = field.getAnnotation(Select2Choice.class);
                widget.setSelect2Choice(select2Choice);
                widget.setOrder(select2Choice.order());

                ChoiceProvider<Serializable> provider = null;
                try {
                    provider = (ChoiceProvider<Serializable>) select2Choice.provider().newInstance();
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                }

                com.vaynberg.wicket.select2.Select2Choice<Serializable> component = new com.vaynberg.wicket.select2.Select2Choice<Serializable>("field", new PropertyModel<Serializable>(model, field.getName()), provider);
                component.setType(field.getType());
                component.getSettings().setMinimumInputLength(select2Choice.minimumInputLength());
                component.add(new DragAndDropBehavior());
                components.put(field.getName(), component);
            }

            if (widget != null) {
                this.fieldTypes.put(field.getName(), field.getType());
                this.fieldController.put(field.getName(), widget);
                if (field.getType().isArray()) {
                    widget.setElementType(field.getType().getComponentType());
                }
                widget.setType(field.getType());
                widget.setName(field.getName());
                widget.setPast(field.getAnnotation(Past.class));
                widget.setUnique(field.getAnnotation(Unique.class));
                widget.setDigits(field.getAnnotation(Digits.class));
                widget.setDecimalMax(field.getAnnotation(DecimalMax.class));
                widget.setDecimalMin(field.getAnnotation(DecimalMin.class));
                widget.setFuture(field.getAnnotation(Future.class));
                widget.setSize(field.getAnnotation(Size.class));
                widget.setNotNull(field.getAnnotation(NotNull.class));
                widget.setMin(field.getAnnotation(Min.class));
                widget.setMax(field.getAnnotation(Max.class));
                widget.setPattern(field.getAnnotation(Pattern.class));
                fields.add(widget);
            }
        }

        Collections.sort(fields);

        ListView<FieldController> widgetListView = new ListView<FieldController>("fields", fields) {

            /**
             * 
             */
            private static final long serialVersionUID = 8700106567169972527L;

            @Override
            protected void populateItem(ListItem<FieldController> item) {
                FieldController widget = item.getModelObject();
                if (widget.getTextField() != null) {
                    TextFieldType widgetType = widget.getTextField().type();
                    if (widgetType == TextFieldType.EMAIL || widgetType == TextFieldType.NUMBER || widgetType == TextFieldType.REGX || widgetType == TextFieldType.TEXT || widgetType == TextFieldType.URL) {
                        WidgetTextField widgetTextField = new WidgetTextField("field", model, widget, components, form);
                        item.add(widgetTextField);
                    } else if (widgetType == TextFieldType.DATETIME) {
                        WidgetDateTimeField widgetDateTimeField = new WidgetDateTimeField("field", model, widget, components);
                        item.add(widgetDateTimeField);
                    } else if (widgetType == TextFieldType.DATE) {
                        WidgetDateField widgetDateField = new WidgetDateField("field", model, widget, components);
                        item.add(widgetDateField);
                    } else if (widgetType == TextFieldType.TIME) {
                        WidgetTimeField widgetTimeField = new WidgetTimeField("field", model, widget, components);
                        item.add(widgetTimeField);
                    } else if (widgetType == TextFieldType.PASSWORD) {
                        WidgetPasswordTextField widgetPasswordTextField = new WidgetPasswordTextField("field", model, widget, components, form);
                        item.add(widgetPasswordTextField);
                    }
                }
                if (widget.getLabelField() != null) {
                    WidgetLabelField widgetLabelField = new WidgetLabelField("field", model, widget, components);
                    item.add(widgetLabelField);
                }
                if (widget.getCheckBox() != null) {
                    WidgetCheckBox widgetCheckBox = new WidgetCheckBox("field", model, widget, components);
                    item.add(widgetCheckBox);
                }
                if (widget.getCheckBoxMultipleChoice() != null) {
                    WidgetCheckBoxMultipleChoice widgetCheckBoxMultipleChoice = new WidgetCheckBoxMultipleChoice("field", model, widget, components);
                    item.add(widgetCheckBoxMultipleChoice);
                }
                if (widget.getListMultipleChoice() != null) {
                    WidgetListMultipleChoice widgetListMultipleChoice = new WidgetListMultipleChoice("field", model, widget, components);
                    item.add(widgetListMultipleChoice);
                }
                if (widget.getFileUploadField() != null) {
                    WidgetFileUploadField widgetFileUploadField = new WidgetFileUploadField("field", model, widget, components);
                    item.add(widgetFileUploadField);
                }
                if (widget.getMultiFileUploadField() != null) {
                    WidgetMultiFileUploadField widgetMultiFileUploadField = new WidgetMultiFileUploadField("field", model, widget, components);
                    item.add(widgetMultiFileUploadField);
                }
                if (widget.getRadioChoice() != null) {
                    WidgetRadioChoice widgetRadioChoice = new WidgetRadioChoice("field", model, widget, components);
                    item.add(widgetRadioChoice);
                }
                if (widget.getDropDownChoice() != null) {
                    WidgetDropDownChoice widgetDropDownChoice = new WidgetDropDownChoice("field", model, widget, components);
                    item.add(widgetDropDownChoice);
                }
                if (widget.getTextArea() != null) {
                    WidgetTextArea widgetTextArea = new WidgetTextArea("field", model, widget, components);
                    item.add(widgetTextArea);
                }
                if (widget.getSelect2MultiChoice() != null) {
                    WidgetSelect2MultiChoice widgetSelect2MultiChoice = new WidgetSelect2MultiChoice("field", model, widget, components);
                    item.add(widgetSelect2MultiChoice);
                }
                if (widget.getSelect2Choice() != null) {
                    WidgetSelect2Choice widgetSelect2Choice = new WidgetSelect2Choice("field", model, widget, components);
                    item.add(widgetSelect2Choice);
                }
            }
        };

        form.add(widgetListView);
        widgetListView.setReuseItems(true);

        List<ButtonController> commands = new ArrayList<ButtonController>();
        for (Method method : ReflectionUtils.getAllMethods(this.getClass())) {
            if (method.getAnnotation(com.itrustcambodia.pluggable.widget.Button.class) != null) {
                com.itrustcambodia.pluggable.widget.Button widget = method.getAnnotation(com.itrustcambodia.pluggable.widget.Button.class);
                ButtonController command = new ButtonController();
                command.setName(method.getName());
                command.setButton(widget);
                command.setOrder(widget.order());
                commands.add(command);

                Button button = new Button("button", new Model<String>(command.getName())) {

                    /**
                     * 
                     */
                    private static final long serialVersionUID = -8528703498437930841L;

                    @Override
                    public void onSubmit() {
                        String name = (String) getDefaultModelObject();
                        transfer();
                        buttonClick(name);
                    }

                    @Override
                    public void onError() {
                        String name = (String) getDefaultModelObject();
                        try {
                            Method method = Form.class.getDeclaredMethod("updateFormComponentModels");
                            method.setAccessible(true);
                            method.invoke(getForm());
                        } catch (SecurityException e) {
                        } catch (NoSuchMethodException e) {
                        } catch (IllegalArgumentException e) {
                        } catch (IllegalAccessException e) {
                        } catch (InvocationTargetException e) {
                        }
                        if (!KnownPage.this.buttonController.get(name).getButton().validate()) {
                            buttonClick(name);
                        }
                    }
                };
                this.buttons.put(method.getName(), button);

                this.buttonController.put(method.getName(), command);
            }
        }

        Collections.sort(commands);

        ListView<ButtonController> commandListView = new ListView<ButtonController>("buttons", commands) {

            /**
             * 
             */
            private static final long serialVersionUID = 2265771875020994354L;

            @Override
            protected void populateItem(ListItem<ButtonController> item) {
                ButtonController command = item.getModelObject();
                Button button = (Button) buttons.get(command.getName());

                item.add(button);
                if (command.getButton().type() == com.itrustcambodia.pluggable.validation.type.ButtonType.DEFAULT) {
                    button.add(AttributeModifier.replace("class", "btn btn-default"));
                } else if (command.getButton().type() == com.itrustcambodia.pluggable.validation.type.ButtonType.PRIMARY) {
                    button.add(AttributeModifier.replace("class", "btn btn-primary"));
                } else if (command.getButton().type() == com.itrustcambodia.pluggable.validation.type.ButtonType.SUCCESS) {
                    button.add(AttributeModifier.replace("class", "btn btn-success"));
                } else if (command.getButton().type() == com.itrustcambodia.pluggable.validation.type.ButtonType.INFO) {
                    button.add(AttributeModifier.replace("class", "btn btn-info"));
                } else if (command.getButton().type() == com.itrustcambodia.pluggable.validation.type.ButtonType.WARNING) {
                    button.add(AttributeModifier.replace("class", "btn btn-warning"));
                } else if (command.getButton().type() == com.itrustcambodia.pluggable.validation.type.ButtonType.DANGER) {
                    button.add(AttributeModifier.replace("class", "btn btn-danger"));
                } else if (command.getButton().type() == com.itrustcambodia.pluggable.validation.type.ButtonType.LINK) {
                    button.add(AttributeModifier.replace("class", "btn btn-link"));
                }

                Label label = new Label("label", command.getButton().label());
                button.add(label);
            }
        };
        form.add(commandListView);
        commandListView.setReuseItems(true);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void transfer() {

        for (Field field : ReflectionUtils.getAllFields(this.getClass())) {
            Class<?> fieldType = this.fieldTypes.get(field.getName());
            if (field.getAnnotation(CheckBox.class) != null) {
                if (model.get(field.getName()) == null) {
                    try {
                        FieldUtils.writeField(field, this, false, true);
                    } catch (IllegalAccessException e) {
                    }
                    continue;
                }
                Boolean value = (Boolean) this.model.get(field.getName());
                if (fieldType.getName().equals("java.lang.Boolean") || fieldType.getName().equals("boolean")) {
                    try {
                        FieldUtils.writeField(field, this, value, true);
                    } catch (IllegalAccessException e) {
                    }
                } else if (fieldType.getName().equals("java.lang.String")) {
                    try {
                        FieldUtils.writeField(field, this, String.valueOf(value), true);
                    } catch (IllegalAccessException e) {
                    }
                }
            }

            if (field.getAnnotation(CheckBoxMultipleChoice.class) != null || field.getAnnotation(ListMultipleChoice.class) != null) {
                if (model.get(field.getName()) == null || ((List<?>) model.get(field.getName())).isEmpty()) {
                    try {
                        FieldUtils.writeField(field, this, null, true);
                    } catch (IllegalAccessException e) {
                    }
                    continue;
                }

                if (int[].class.isAssignableFrom(field.getType())) {
                    List<Map<String, String>> list = (List<Map<String, String>>) model.get(field.getName());
                    int[] value = new int[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        value[i] = Integer.valueOf(list.get(i).get("value"));
                    }
                    try {
                        FieldUtils.writeField(field, this, value, true);
                    } catch (IllegalAccessException e) {
                    }
                } else if (Integer[].class.isAssignableFrom(field.getType())) {
                    List<Map<String, String>> list = (List<Map<String, String>>) model.get(field.getName());
                    Integer[] value = new Integer[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        value[i] = Integer.valueOf(list.get(i).get("value"));
                    }
                    try {
                        FieldUtils.writeField(field, this, value, true);
                    } catch (IllegalAccessException e) {
                    }
                } else if (short[].class.isAssignableFrom(field.getType())) {
                    List<Map<String, String>> list = (List<Map<String, String>>) model.get(field.getName());
                    short[] value = new short[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        value[i] = Short.valueOf(list.get(i).get("value"));
                    }
                    try {
                        FieldUtils.writeField(field, this, value, true);
                    } catch (IllegalAccessException e) {
                    }
                } else if (Short[].class.isAssignableFrom(field.getType())) {
                    List<Map<String, String>> list = (List<Map<String, String>>) model.get(field.getName());
                    Short[] value = new Short[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        value[i] = Short.valueOf(list.get(i).get("value"));
                    }
                    try {
                        FieldUtils.writeField(field, this, value, true);
                    } catch (IllegalAccessException e) {
                    }
                } else if (byte[].class.isAssignableFrom(field.getType())) {
                    List<Map<String, String>> list = (List<Map<String, String>>) model.get(field.getName());
                    byte[] value = new byte[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        value[i] = Byte.valueOf(list.get(i).get("value"));
                    }
                    try {
                        FieldUtils.writeField(field, this, value, true);
                    } catch (IllegalAccessException e) {
                    }
                } else if (Byte[].class.isAssignableFrom(field.getType())) {
                    List<Map<String, String>> list = (List<Map<String, String>>) model.get(field.getName());
                    Byte[] value = new Byte[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        value[i] = Byte.valueOf(list.get(i).get("value"));
                    }
                    try {
                        FieldUtils.writeField(field, this, value, true);
                    } catch (IllegalAccessException e) {
                    }
                } else if (Float[].class.isAssignableFrom(field.getType())) {
                    List<Map<String, String>> list = (List<Map<String, String>>) model.get(field.getName());
                    Float[] value = new Float[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        value[i] = Float.valueOf(list.get(i).get("value"));
                    }
                    try {
                        FieldUtils.writeField(field, this, value, true);
                    } catch (IllegalAccessException e) {
                    }
                } else if (float[].class.isAssignableFrom(field.getType())) {
                    List<Map<String, String>> list = (List<Map<String, String>>) model.get(field.getName());
                    float[] value = new float[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        value[i] = Float.valueOf(list.get(i).get("value"));
                    }
                    try {
                        FieldUtils.writeField(field, this, value, true);
                    } catch (IllegalAccessException e) {
                    }
                } else if (Long[].class.isAssignableFrom(field.getType())) {
                    List<Map<String, String>> list = (List<Map<String, String>>) model.get(field.getName());
                    Long[] value = new Long[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        value[i] = Long.valueOf(list.get(i).get("value"));
                    }
                    try {
                        FieldUtils.writeField(field, this, value, true);
                    } catch (IllegalAccessException e) {
                    }
                } else if (long[].class.isAssignableFrom(field.getType())) {
                    List<Map<String, String>> list = (List<Map<String, String>>) model.get(field.getName());
                    long[] value = new long[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        value[i] = Long.valueOf(list.get(i).get("value"));
                    }
                    try {
                        FieldUtils.writeField(field, this, value, true);
                    } catch (IllegalAccessException e) {
                    }
                } else if (Double[].class.isAssignableFrom(field.getType())) {
                    List<Map<String, String>> list = (List<Map<String, String>>) model.get(field.getName());
                    Double[] value = new Double[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        value[i] = Double.valueOf(list.get(i).get("value"));
                    }
                    try {
                        FieldUtils.writeField(field, this, value, true);
                    } catch (IllegalAccessException e) {
                    }
                } else if (double[].class.isAssignableFrom(field.getType())) {
                    List<Map<String, String>> list = (List<Map<String, String>>) model.get(field.getName());
                    double[] value = new double[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        value[i] = Double.valueOf(list.get(i).get("value"));
                    }
                    try {
                        FieldUtils.writeField(field, this, value, true);
                    } catch (IllegalAccessException e) {
                    }
                } else if (String[].class.isAssignableFrom(field.getType())) {
                    List<Map<String, String>> list = (List<Map<String, String>>) model.get(field.getName());
                    String[] value = new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        value[i] = list.get(i).get("value");
                    }
                    try {
                        FieldUtils.writeField(field, this, value, true);
                    } catch (IllegalAccessException e) {
                    }
                } else {
                    List<Map<String, String>> list = (List<Map<String, String>>) model.get(field.getName());
                    String id = TableUtilities.getIdentityField(field.getType().getComponentType());

                    AbstractWebApplication application = (AbstractWebApplication) getApplication();
                    String tableName = TableUtilities.getTableName(field.getType().getComponentType());
                    String query = "select * from " + tableName + " where " + id + " = ?";
                    Object[] value = (Object[]) Array.newInstance(field.getType().getComponentType(), list.size());
                    for (int i = 0; i < list.size(); i++) {
                        try {
                            value[i] = application.getJdbcTemplate().queryForObject(query, new EntityRowMapper(field.getType().getComponentType()), list.get(i).get("value"));
                        } catch (EmptyResultDataAccessException e) {
                        }
                    }
                    try {
                        FieldUtils.writeField(field, this, value, true);
                    } catch (IllegalAccessException e) {
                    }
                }
            }

            if (field.getAnnotation(DropDownChoice.class) != null || field.getAnnotation(RadioChoice.class) != null) {
                Map<String, String> value = (Map<String, String>) model.get(field.getName());
                if (value == null || value.isEmpty()) {
                    try {
                        FieldUtils.writeField(field, this, null, true);
                    } catch (IllegalAccessException e) {
                    }
                    continue;
                }
                if (fieldType.getName().equals("java.lang.Short") || fieldType.getName().equals("short")) {
                    try {
                        FieldUtils.writeField(field, this, Short.valueOf(value.get("value")), true);
                    } catch (NumberFormatException e) {
                    } catch (IllegalAccessException e) {
                    }
                } else if (fieldType.getName().equals("java.lang.Long") || fieldType.getName().equals("long")) {
                    try {
                        FieldUtils.writeField(field, this, Long.valueOf(value.get("value")), true);
                    } catch (NumberFormatException e) {
                    } catch (IllegalAccessException e) {
                    }
                } else if (fieldType.getName().equals("java.lang.Integer") || fieldType.getName().equals("int")) {
                    try {
                        FieldUtils.writeField(field, this, Integer.valueOf(value.get("value")), true);
                    } catch (NumberFormatException e) {
                    } catch (IllegalAccessException e) {
                    }
                } else if (fieldType.getName().equals("java.lang.Byte") || fieldType.getName().equals("byte")) {
                    try {
                        FieldUtils.writeField(field, this, Byte.valueOf(value.get("value")), true);
                    } catch (NumberFormatException e) {
                    } catch (IllegalAccessException e) {
                    }
                } else if (fieldType.getName().equals("java.lang.Float") || fieldType.getName().equals("float")) {
                    try {
                        FieldUtils.writeField(field, this, Float.valueOf(value.get("value")), true);
                    } catch (NumberFormatException e) {
                    } catch (IllegalAccessException e) {
                    }
                } else if (fieldType.getName().equals("java.lang.Double") || fieldType.getName().equals("double")) {
                    try {
                        FieldUtils.writeField(field, this, Double.valueOf(value.get("value")), true);
                    } catch (NumberFormatException e) {
                    } catch (IllegalAccessException e) {
                    }
                } else if (fieldType.getName().equals("java.lang.String")) {
                    try {
                        FieldUtils.writeField(field, this, value.get("value"), true);
                    } catch (IllegalAccessException e) {
                    }
                } else {
                    String id = TableUtilities.getIdentityField(field.getType());

                    AbstractWebApplication application = (AbstractWebApplication) getApplication();
                    String tableName = TableUtilities.getTableName(field.getType());
                    String query = "select * from " + tableName + " where " + id + " = ?";
                    try {
                        Object entity = application.getJdbcTemplate().queryForObject(query, new EntityRowMapper(field.getType()), value.get("value"));
                        try {
                            FieldUtils.writeField(field, this, entity, true);
                        } catch (IllegalAccessException e) {
                        }
                    } catch (EmptyResultDataAccessException e) {
                    }
                }
            }

            if (field.getAnnotation(FileUploadField.class) != null) {
                List<FileUpload> values = (List<FileUpload>) model.get(field.getName());
                if (values == null || values.isEmpty()) {
                    try {
                        FieldUtils.writeField(field, this, null, true);
                    } catch (IllegalAccessException e) {
                    }
                    continue;
                }
                try {
                    FieldUtils.writeField(field, this, values.get(0), true);
                } catch (IllegalAccessException e) {
                }
            }

            if (field.getAnnotation(MultiFileUploadField.class) != null) {
                List<FileUpload> values = (List<FileUpload>) model.get(field.getName());
                if (values == null || values.isEmpty()) {
                    try {
                        FieldUtils.writeField(field, this, null, true);
                    } catch (IllegalAccessException e) {
                    }
                    continue;
                }
                try {
                    FieldUtils.writeField(field, this, Arrays.asList(values), true);
                } catch (IllegalAccessException e) {
                }
            }

            if (field.getAnnotation(Select2MultiChoice.class) != null) {
                List<?> list = (List<?>) model.get(field.getName());
                if (list == null || list.isEmpty()) {
                    try {
                        FieldUtils.writeField(field, this, null, true);
                    } catch (IllegalAccessException e) {
                    }
                    continue;
                }
                Object[] objects = (Object[]) Array.newInstance(field.getType().getComponentType(), list.size());
                for (int i = 0; i < list.size(); i++) {
                    objects[i] = list.get(i);
                }
                try {
                    FieldUtils.writeField(field, this, objects, true);
                } catch (IllegalAccessException e) {
                }
            }
            if (field.getAnnotation(Select2Choice.class) != null) {
                Object object = model.get(field.getName());
                if (object == null || "".equals(object)) {
                    try {
                        FieldUtils.writeField(field, this, null, true);
                    } catch (IllegalAccessException e) {
                    }
                    continue;
                }
                try {
                    FieldUtils.writeField(field, this, object, true);
                } catch (IllegalAccessException e) {
                }
            }

            if (field.getAnnotation(TextArea.class) != null) {
                String value = (String) model.get(field.getName());
                if (value == null || "".equals(value)) {
                    try {
                        FieldUtils.writeField(field, this, null, true);
                    } catch (IllegalAccessException e) {
                    }
                    continue;
                }
                try {
                    FieldUtils.writeField(field, this, value, true);
                } catch (IllegalAccessException e) {
                }
            }

            if (field.getAnnotation(TextField.class) != null) {
                String value = (String) model.get(field.getName());
                if (value == null || "".equals(value)) {
                    try {
                        FieldUtils.writeField(field, this, null, true);
                    } catch (IllegalAccessException e) {
                    }
                    continue;
                }
                if (fieldType.getName().equals("java.lang.Short") || fieldType.getName().equals("short")) {
                    try {
                        FieldUtils.writeField(field, this, Short.valueOf(value), true);
                    } catch (NumberFormatException e) {
                    } catch (IllegalAccessException e) {
                    }
                } else if (fieldType.getName().equals("java.lang.Long") || fieldType.getName().equals("long")) {
                    try {
                        FieldUtils.writeField(field, this, Long.valueOf(value), true);
                    } catch (NumberFormatException e) {
                    } catch (IllegalAccessException e) {
                    }
                } else if (fieldType.getName().equals("java.lang.Integer") || fieldType.getName().equals("int")) {
                    try {
                        FieldUtils.writeField(field, this, Integer.valueOf(value), true);
                    } catch (NumberFormatException e) {
                    } catch (IllegalAccessException e) {
                    }
                } else if (fieldType.getName().equals("java.lang.Byte") || fieldType.getName().equals("byte")) {
                    try {
                        FieldUtils.writeField(field, this, Byte.valueOf(value), true);
                    } catch (NumberFormatException e) {
                    } catch (IllegalAccessException e) {
                    }
                } else if (fieldType.getName().equals("java.lang.Float") || fieldType.getName().equals("float")) {
                    try {
                        FieldUtils.writeField(field, this, Float.valueOf(value), true);
                    } catch (NumberFormatException e) {
                    } catch (IllegalAccessException e) {
                    }
                } else if (fieldType.getName().equals("java.lang.Double") || fieldType.getName().equals("double")) {
                    try {
                        FieldUtils.writeField(field, this, Double.valueOf(value), true);
                    } catch (NumberFormatException e) {
                    } catch (IllegalAccessException e) {
                    }
                } else if (fieldType.getName().equals("java.lang.String")) {
                    try {
                        FieldUtils.writeField(field, this, value, true);
                    } catch (IllegalAccessException e) {
                    }
                } else if (fieldType.getName().equals("java.util.Date")) {
                    SimpleDateFormat format = new SimpleDateFormat(field.getAnnotation(TextField.class).pattern());
                    try {
                        FieldUtils.writeField(field, this, format.parseObject(value), true);
                    } catch (IllegalAccessException e) {
                    } catch (ParseException e) {
                        try {
                            Date val = (Date) FieldUtils.readField(field, this, true);
                            if (val != null) {
                                model.put(field.getName(), format.format(val));
                            }
                        } catch (IllegalAccessException e1) {
                        }

                    }
                }
            }
        }
    }

    protected final void buttonClick(String methodName) {
        try {
            Method method = this.getClass().getMethod(methodName);
            Navigation navigation = (Navigation) method.invoke(this);
            if (navigation != null) {
                if (navigation.getInstance() != null) {
                    setResponsePage(navigation.getInstance());
                }
                if (navigation.getClazz() != null) {
                    if (navigation.getParameters() != null) {
                        setResponsePage(navigation.getClazz(), navigation.getParameters());
                    } else {
                        setResponsePage(navigation.getClazz());
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
