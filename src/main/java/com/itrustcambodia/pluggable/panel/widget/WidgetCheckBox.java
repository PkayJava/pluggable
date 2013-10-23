package com.itrustcambodia.pluggable.panel.widget;

import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.itrustcambodia.pluggable.panel.InputFeedback;
import com.itrustcambodia.pluggable.validation.controller.FieldController;

public class WidgetCheckBox extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7060419016983447006L;

    public WidgetCheckBox(String id, Map<String, Object> object, FieldController widget, Map<String, Object> components) {
        super(id);

        CheckBox checkBox = (CheckBox) components.get(widget.getName());

        checkBox.setLabel(new Model<String>(widget.getCheckBox().label()));
        if (widget.getNotNull() != null) {
            checkBox.setRequired(true);
        }

        add(checkBox);

        InputFeedback feedback = new InputFeedback("feedback", checkBox);
        add(feedback);

        Label label = new Label("label", widget.getCheckBox().label());
        add(label);

        Label placeholder = new Label("placeholder", widget.getCheckBox().placeholder());
        add(placeholder);

    }
}
