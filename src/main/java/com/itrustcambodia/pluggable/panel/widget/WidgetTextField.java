package com.itrustcambodia.pluggable.panel.widget;

import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.PatternValidator;

import com.itrustcambodia.pluggable.panel.InputFeedback;
import com.itrustcambodia.pluggable.validation.controller.FieldController;
import com.itrustcambodia.pluggable.validation.type.TextFieldType;
import com.itrustcambodia.pluggable.validator.ByteValidator;
import com.itrustcambodia.pluggable.validator.DoubleValidator;
import com.itrustcambodia.pluggable.validator.EmailAddressValidator;
import com.itrustcambodia.pluggable.validator.FloatValidator;
import com.itrustcambodia.pluggable.validator.IntegerValidator;
import com.itrustcambodia.pluggable.validator.LongValidator;
import com.itrustcambodia.pluggable.validator.ShortValidator;
import com.itrustcambodia.pluggable.validator.UniqueValidator;
import com.itrustcambodia.pluggable.validator.UrlValidator;

public class WidgetTextField extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7060419016983447006L;

    public WidgetTextField(String id, Map<String, Object> object, FieldController widget, Map<String, Object> components, Form<?> form) {
        super(id);

        Label label = new Label("label", widget.getTextField().label());
        add(label);
        TextFieldType widgetType = widget.getTextField().type();
        TextField<String> textField = null;
        if (widgetType == TextFieldType.EMAIL || widgetType == TextFieldType.TEXT || widgetType == TextFieldType.REGX || widgetType == TextFieldType.URL) {
            textField = (TextField<String>) components.get(widget.getName());
            if (widget.getType().getName().equals("java.lang.Byte") || widget.getType().getName().equals("byte")) {
                textField.add(new ByteValidator());
            } else if (widget.getType().getName().equals("java.lang.Long") || widget.getType().getName().equals("long")) {
                textField.add(new LongValidator());
            } else if (widget.getType().getName().equals("java.lang.Double") || widget.getType().getName().equals("double")) {
                textField.add(new DoubleValidator());
            } else if (widget.getType().getName().equals("java.lang.Integer") || widget.getType().getName().equals("int")) {
                textField.add(new IntegerValidator());
            } else if (widget.getType().getName().equals("java.lang.Float") || widget.getType().getName().equals("float")) {
                textField.add(new FloatValidator());
            } else if (widget.getType().getName().equals("java.lang.Short") || widget.getType().getName().equals("short")) {
                textField.add(new ShortValidator());
            } else if (widget.getType().getName().equals("java.lang.String")) {
                if (widget.getTextField().type() == TextFieldType.EMAIL) {
                    textField.add(new EmailAddressValidator());
                } else if (widget.getTextField().type() == TextFieldType.URL) {
                    textField.add(new UrlValidator());
                } else if (widget.getTextField().type() == TextFieldType.REGX) {
                    textField.add(new PatternValidator(widget.getTextField().pattern()));
                }
            } else {
                throw new WicketRuntimeException("unknow type " + widget.getType().getName());
            }
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
