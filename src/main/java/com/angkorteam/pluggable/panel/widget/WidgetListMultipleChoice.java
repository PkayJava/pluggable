package com.angkorteam.pluggable.panel.widget;

import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.angkorteam.pluggable.panel.InputFeedback;
import com.angkorteam.pluggable.validation.controller.FieldController;

/**
 * @author Socheat KHAUV
 */
public class WidgetListMultipleChoice extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7060419016983447006L;

    private FieldController widget;

    public WidgetListMultipleChoice(String id, Map<String, Object> object, FieldController widget, Map<String, Object> components) {
        super(id);
        this.widget = widget;

        ListMultipleChoice<?> listMultipleChoice = (ListMultipleChoice<?>) components.get(widget.getName());

        add(listMultipleChoice);

        listMultipleChoice.setLabel(new Model<String>(this.widget.getListMultipleChoice().label()));

        if (this.widget.getNotNull() != null) {
            listMultipleChoice.setRequired(true);
        }

        InputFeedback feedback = new InputFeedback("feedback", listMultipleChoice);
        add(feedback);

        Label label = new Label("label", this.widget.getListMultipleChoice().label());
        add(label);
    }

}
