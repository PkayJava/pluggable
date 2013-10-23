package com.itrustcambodia.pluggable.panel.widget;

import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import com.itrustcambodia.pluggable.validation.controller.FieldController;

/**
 * @author Socheat KHAUV
 */
public class WidgetLabelField extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7060419016983447006L;

    public WidgetLabelField(String id, Map<String, Object> object, FieldController widget, Map<String, Object> components) {
        super(id);

        Label label = new Label("label", widget.getLabelField().label());
        add(label);

        Label field = (Label) components.get(widget.getName());

        if (object.get(widget.getName()) != null) {
            field.setDefaultModelObject(object.get(widget.getName()));
        }
        add(field);

    }

}
