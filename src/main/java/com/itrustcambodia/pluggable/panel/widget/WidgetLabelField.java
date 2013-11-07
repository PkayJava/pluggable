package com.itrustcambodia.pluggable.panel.widget;

import java.util.Map;

import org.apache.wicket.AttributeModifier;
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
        Label link = new Label("link","");
        if (object.get(widget.getName()) != null) {
            if (object.get(widget.getName()) instanceof String) {
                com.itrustcambodia.pluggable.validator.UrlValidator urlValidator = new com.itrustcambodia.pluggable.validator.UrlValidator();
                if (urlValidator.isValid((String) object.get(widget.getName()))) {
                    link.setVisible(true);
                    field.setVisible(false);
                    link.setDefaultModelObject(object.get(widget.getName()));
                    link.add(AttributeModifier.replace("href", (String) object.get(widget.getName())));
                } else {
                    link.setVisible(false);
                    field.setVisible(true);
                }
            } else {
                link.setVisible(false);
                field.setVisible(true);
            }
        } else {
            link.setVisible(false);
            field.setVisible(false);
        }

        if (object.get(widget.getName()) != null) {
            field.setDefaultModelObject(object.get(widget.getName()));
        }
        add(link);
        add(field);

    }
}
