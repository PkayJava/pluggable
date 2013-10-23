package com.itrustcambodia.pluggable.panel.widget;

import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.itrustcambodia.pluggable.panel.InputFeedback;
import com.itrustcambodia.pluggable.validation.controller.FieldController;
import com.itrustcambodia.pluggable.validator.DateValidator;

public class WidgetDateTimeField extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7060419016983447006L;

    public WidgetDateTimeField(String id, Map<String, Object> object, FieldController widget, Map<String, Object> components) {
        super(id);

        Label label = new Label("label", widget.getTextField().label());
        add(label);

        TextField<String> textField = (TextField<String>) components.get(widget.getName());
        add(textField);

        if (widget.getTextField().placeholder() == null || "".equals(widget.getTextField().placeholder())) {
            textField.add(AttributeModifier.replace("placeholder", widget.getTextField().pattern()));
        } else {
            textField.add(AttributeModifier.replace("placeholder", widget.getTextField().placeholder()));
        }

        textField.add(new DateValidator(widget.getTextField().pattern()));

        textField.setLabel(new Model<String>(widget.getTextField().label()));
        if (widget.getNotNull() != null) {
            textField.setRequired(true);
        }

        InputFeedback feedback = new InputFeedback("feedback", textField);
        add(feedback);

    }

}
