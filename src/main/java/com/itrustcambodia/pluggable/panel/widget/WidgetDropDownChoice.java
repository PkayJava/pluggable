package com.itrustcambodia.pluggable.panel.widget;

import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.itrustcambodia.pluggable.panel.InputFeedback;
import com.itrustcambodia.pluggable.validation.controller.FieldController;

/**
 * @author Socheat KHAUV
 */
public class WidgetDropDownChoice extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7060419016983447006L;

    private FieldController widget;

    private List<Map<String, String>> choices;

    public WidgetDropDownChoice(String id, Map<String, Object> object, FieldController widget, Map<String, Object> components) {
        super(id);
        this.widget = widget;

        DropDownChoice<Map<String, String>> dropDownChoice = (DropDownChoice<Map<String, String>>) components.get(widget.getName());

        add(dropDownChoice);

        dropDownChoice.setLabel(new Model<String>(this.widget.getDropDownChoice().label()));

        if (this.widget.getNotNull() != null) {
            dropDownChoice.setRequired(true);
        }

        InputFeedback feedback = new InputFeedback("feedback", dropDownChoice);
        add(feedback);

        Label label = new Label("label", this.widget.getDropDownChoice().label());
        add(label);
    }
}
