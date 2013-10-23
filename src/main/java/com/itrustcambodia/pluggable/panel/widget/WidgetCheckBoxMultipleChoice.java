package com.itrustcambodia.pluggable.panel.widget;

import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.itrustcambodia.pluggable.panel.InputFeedback;
import com.itrustcambodia.pluggable.validation.controller.FieldController;

/**
 * @author Socheat KHAUV
 */
public class WidgetCheckBoxMultipleChoice extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7060419016983447006L;

    public WidgetCheckBoxMultipleChoice(String id, Map<String, Object> object, FieldController widget, Map<String, Object> components) {
        super(id);

        CheckBoxMultipleChoice<?> checkBoxMultipleChoice = (CheckBoxMultipleChoice<?>) components.get(widget.getName());

        checkBoxMultipleChoice.setLabel(new Model<String>(widget.getCheckBoxMultipleChoice().label()));
        if (widget.getNotNull() != null) {
            checkBoxMultipleChoice.setRequired(true);
        }

        add(checkBoxMultipleChoice);

        InputFeedback feedback = new InputFeedback("feedback", checkBoxMultipleChoice);
        add(feedback);

        Label label = new Label("label", widget.getCheckBoxMultipleChoice().label());
        add(label);
    }

}
