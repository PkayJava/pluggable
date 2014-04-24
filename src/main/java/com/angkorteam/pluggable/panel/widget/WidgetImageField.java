package com.angkorteam.pluggable.panel.widget;

import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import com.angkorteam.pluggable.validation.controller.FieldController;
import com.angkorteam.pluggable.wicket.markup.html.image.Image;

/**
 * @author Socheat KHAUV
 */
public class WidgetImageField extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7060419016983447006L;

    public WidgetImageField(String id, Map<String, Object> object,
            FieldController widget, Map<String, Object> components) {
        super(id);

        Label label = new Label("label", widget.getImageField().label());
        add(label);

        Image field = (Image) components.get(widget.getName());

        add(field);

    }
}
