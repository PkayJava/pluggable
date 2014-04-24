package com.angkorteam.pluggable.panel.widget;

import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.angkorteam.pluggable.panel.InputFeedback;
import com.angkorteam.pluggable.validation.controller.FieldController;

/**
 * @author Socheat KHAUV
 */
public class WidgetTextArea extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7060419016983447006L;

    private FieldController widget;

    public WidgetTextArea(String id, Map<String, Object> object, FieldController widget, Map<String, Object> components) {
        super(id);
        this.widget = widget;

        Label label = new Label("label", this.widget.getTextArea().label());
        add(label);
        TextArea<?> textArea = (TextArea<?>) components.get(widget.getName());
        add(textArea); 

        textArea.setLabel(new Model<String>(this.widget.getTextArea().label()));

        if (this.widget.getNotNull() != null) {
            textArea.setRequired(true);
        }

        InputFeedback feedback = new InputFeedback("feedback", textArea);
        add(feedback);

    }
}
