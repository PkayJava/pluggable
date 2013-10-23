package com.itrustcambodia.pluggable.panel.form;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import com.itrustcambodia.pluggable.core.FormItem;
import com.itrustcambodia.pluggable.panel.InputFeedback;

public class SingleChoiceDropDownFieldPanel<T> extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7570775056642385303L;

    public SingleChoiceDropDownFieldPanel(String id, FormItem<T> formItem) {
        super(id);
        add(new Label("label", formItem.getLabel()));

        DropDownChoice<T> userInput = new DropDownChoice<T>("userInput", new PropertyModel<T>(formItem, "userInput"), formItem.getList());

        userInput.setRequired(formItem.isRequired());
        add(userInput);

        InputFeedback feedback = new InputFeedback("feedback", userInput);
        add(feedback);

        HiddenField<String> hiddenField = new HiddenField<String>("inputName", new PropertyModel<String>(formItem, "inputName"));
        add(hiddenField);
    }
}
