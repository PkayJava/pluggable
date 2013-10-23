package com.itrustcambodia.pluggable.panel.form;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import com.itrustcambodia.pluggable.core.FormItem;

public class ToggleFieldPanel extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7570775056642385303L;

    public ToggleFieldPanel(String id, FormItem<Boolean> formItem) {
        super(id);
        add(new Label("label", formItem.getLabel()));

        CheckBox userInput = new CheckBox("userInput", new PropertyModel<Boolean>(formItem, "userInput"));
        add(userInput);

        Label placeholder = new Label("placeholder", formItem.getPlaceholder());
        add(placeholder);

        HiddenField<String> hiddenField = new HiddenField<String>("inputName", new PropertyModel<String>(formItem, "inputName"));
        add(hiddenField);
    }
}
