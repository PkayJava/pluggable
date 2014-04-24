package com.angkorteam.pluggable.panel.widget;

import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.angkorteam.pluggable.panel.InputFeedback;
import com.angkorteam.pluggable.validation.controller.FieldController;
import com.angkorteam.pluggable.validation.type.TextFieldType;
import com.angkorteam.pluggable.validator.UniqueValidator;

/**
 * @author Socheat KHAUV
 */
public class WidgetPasswordTextField extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7060419016983447006L;

    public WidgetPasswordTextField(String id, Map<String, Object> object, FieldController widget, Map<String, Object> components, Form<?> form) {
        super(id);

        Label label = new Label("label", widget.getTextField().label());
        add(label);
        TextFieldType widgetType = widget.getTextField().type();
        TextField<String> textField = null;
        if (widgetType == TextFieldType.PASSWORD) {
            textField = (PasswordTextField) components.get(widget.getName());
        }

        add(textField);
        if (widget.getUnique() != null) {
            UniqueValidator validator = new UniqueValidator(widget.getUnique(), object, textField, components);
            form.add(validator);
        }

        textField.setLabel(new Model<String>(widget.getTextField().label()));
        if (widget.getNotNull() != null) {
            textField.setRequired(true);
        }

        textField.add(AttributeModifier.replace("placeholder", widget.getTextField().placeholder()));
        InputFeedback feedback = new InputFeedback("feedback", textField);
        add(feedback);
    }

}
