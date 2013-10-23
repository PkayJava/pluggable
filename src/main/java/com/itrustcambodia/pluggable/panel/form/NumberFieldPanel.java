package com.itrustcambodia.pluggable.panel.form;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import com.itrustcambodia.pluggable.core.FormItem;
import com.itrustcambodia.pluggable.panel.InputFeedback;

public class NumberFieldPanel extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7570775056642385303L;

    public NumberFieldPanel(String id, FormItem<? extends Number> formItem) {
        super(id);
        add(new Label("label", formItem.getLabel()));

        NumberTextField<? extends Number> userInput = null;
        if (formItem.getType() == FormItem.Type.INPUT_NUMBER) {
            userInput = new NumberTextField<Long>("userInput", new PropertyModel<Long>(formItem, "userInput"));
        } else if (formItem.getType() == FormItem.Type.INPUT_DECIMAL) {
            userInput = new NumberTextField<Double>("userInput", new PropertyModel<Double>(formItem, "userInput"));
        }

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
