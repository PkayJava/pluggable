package com.itrustcambodia.pluggable.panel.widget;

import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.itrustcambodia.pluggable.panel.InputFeedback;
import com.itrustcambodia.pluggable.validation.controller.FieldController;

public class WidgetFileUploadField extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7060419016983447006L;

    private FieldController widget;

    public WidgetFileUploadField(String id, Map<String, Object> object, FieldController widget, Map<String, Object> components) {
        super(id);
        this.widget = widget;

        FileUploadField fileUploadField = (FileUploadField) components.get(widget.getName());
        add(fileUploadField);

        fileUploadField.setLabel(new Model<String>(this.widget.getFileUploadField().label()));

        if (this.widget.getNotNull() != null) {
            fileUploadField.setRequired(true);
        }

        InputFeedback feedback = new InputFeedback("feedback", fileUploadField);
        add(feedback);

        Label label = new Label("label", this.widget.getFileUploadField().label());
        add(label);
    }
}
