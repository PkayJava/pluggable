package com.itrustcambodia.pluggable.panel.form;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import com.itrustcambodia.pluggable.core.FormItem;
import com.itrustcambodia.pluggable.panel.InputFeedback;

public class PasswordFieldPanel extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7570775056642385303L;

    public PasswordFieldPanel(String id, FormItem<String> formItem) {
        super(id);
        add(new Label("label", formItem.getLabel()));

        PasswordTextField userInput = new PasswordTextField("userInput", new PropertyModel<String>(formItem, "userInput"));
        userInput.setResetPassword(false);
        userInput.add(AttributeModifier.replace("placeholder", formItem.getPlaceholder()));
        add(userInput);

        userInput.setRequired(formItem.isRequired());

        InputFeedback feedback = new InputFeedback("feedback", userInput);
        add(feedback);

        HiddenField<String> hiddenField = new HiddenField<String>("inputName", new PropertyModel<String>(formItem, "inputName"));
        add(hiddenField);
    }
}
