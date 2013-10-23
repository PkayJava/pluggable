package com.itrustcambodia.pluggable.panel.form;

import java.util.Date;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import com.itrustcambodia.pluggable.core.FormItem;
import com.itrustcambodia.pluggable.panel.InputFeedback;

public class DateTimeFieldPanel extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7570775056642385303L;

    public DateTimeFieldPanel(String id, FormItem<Date> formItem) {
        super(id);
        add(new Label("label", formItem.getLabel()));

        DateTextField userInput = DateTextField.forDatePattern("userInput", new PropertyModel<Date>(formItem, "userInput"), formItem.getPattern());
        userInput.add(AttributeModifier.replace("placeholder", formItem.getPlaceholder()));
        add(userInput);

        userInput.setType(formItem.getClassType());
        userInput.setRequired(formItem.isRequired());

        InputFeedback feedback = new InputFeedback("feedback", userInput);
        add(feedback);

        HiddenField<String> hiddenField = new HiddenField<String>("inputName", new PropertyModel<String>(formItem, "inputName"));
        add(hiddenField);
    }
}
