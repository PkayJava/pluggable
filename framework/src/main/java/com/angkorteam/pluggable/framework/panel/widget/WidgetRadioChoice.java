package com.angkorteam.pluggable.framework.panel.widget;

import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.angkorteam.pluggable.framework.panel.InputFeedback;
import com.angkorteam.pluggable.framework.validation.controller.FieldController;

/**
 * @author Socheat KHAUV
 */
public class WidgetRadioChoice extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7060419016983447006L;

    private FieldController widget;

    public WidgetRadioChoice(String id, Map<String, Object> object, FieldController widget, Map<String, Object> components) {
        super(id);
        this.widget = widget;

        RadioChoice<?> radioChoice = (RadioChoice<?>) components.get(widget.getName());

        add(radioChoice); 

        radioChoice.setLabel(new Model<String>(this.widget.getRadioChoice().label()));

        if (this.widget.getNotNull() != null) {
            radioChoice.setRequired(true);
        }

        InputFeedback feedback = new InputFeedback("feedback", radioChoice);
        add(feedback);

        Label label = new Label("label", this.widget.getRadioChoice().label());
        add(label);
    }

}
