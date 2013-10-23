package com.itrustcambodia.pluggable.panel.widget;

import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.itrustcambodia.pluggable.panel.InputFeedback;
import com.itrustcambodia.pluggable.validation.controller.FieldController;
import com.vaynberg.wicket.select2.Select2Choice;

/**
 * @author Socheat KHAUV
 */
public class WidgetSelect2Choice extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7060419016983447006L;

    public WidgetSelect2Choice(String id, Map<String, Object> object, FieldController widget, Map<String, Object> components) {
        super(id);

        Label label = new Label("label", widget.getSelect2Choice().label());
        add(label);

        Select2Choice<?> select2Choice = (Select2Choice<?>) components.get(widget.getName());

        add(select2Choice);

        select2Choice.setLabel(new Model<String>(widget.getSelect2Choice().label()));
        if (widget.getNotNull() != null) {
            select2Choice.setRequired(true);
        }

        InputFeedback feedback = new InputFeedback("feedback", select2Choice);
        add(feedback);
    }

}
