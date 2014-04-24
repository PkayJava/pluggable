package com.angkorteam.pluggable.panel.widget;

import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.angkorteam.pluggable.panel.InputFeedback;
import com.angkorteam.pluggable.validation.controller.FieldController;
import com.vaynberg.wicket.select2.Select2MultiChoice;

/**
 * @author Socheat KHAUV
 */
public class WidgetSelect2MultiChoice extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7060419016983447006L;

    public WidgetSelect2MultiChoice(String id, Map<String, Object> object, FieldController widget, Map<String, Object> components) {
        super(id);

        Label label = new Label("label", widget.getSelect2MultiChoice().label());
        add(label);

        Select2MultiChoice<?> select2MultiChoice = (Select2MultiChoice<?>) components.get(widget.getName());

        add(select2MultiChoice);

        select2MultiChoice.setLabel(new Model<String>(widget.getSelect2MultiChoice().label()));
        if (widget.getNotNull() != null) {
            select2MultiChoice.setRequired(true);
        }

        InputFeedback feedback = new InputFeedback("feedback", select2MultiChoice);
        add(feedback);
    }

}
