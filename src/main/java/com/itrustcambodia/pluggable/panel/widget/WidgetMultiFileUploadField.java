package com.itrustcambodia.pluggable.panel.widget;

import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.upload.MultiFileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.itrustcambodia.pluggable.panel.InputFeedback;
import com.itrustcambodia.pluggable.validation.controller.FieldController;

public class WidgetMultiFileUploadField extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7060419016983447006L;

    private FieldController widget;

    public WidgetMultiFileUploadField(String id, Map<String, Object> object, FieldController widget, Map<String, Object> components) {
        super(id);
        this.widget = widget;

        MultiFileUploadField multiFileUploadField = (MultiFileUploadField) components.get(widget.getName());

        add(multiFileUploadField);

        multiFileUploadField.add(AttributeModifier.replace("class", "wicket-mfu-field form-control"));

        multiFileUploadField.setLabel(new Model<String>(this.widget.getMultiFileUploadField().label()));

        if (this.widget.getNotNull() != null) {
            multiFileUploadField.setRequired(true);
        }

        InputFeedback feedback = new InputFeedback("feedback", multiFileUploadField);
        add(feedback);

        Label label = new Label("label", this.widget.getMultiFileUploadField().label());
        add(label);
    }
}
