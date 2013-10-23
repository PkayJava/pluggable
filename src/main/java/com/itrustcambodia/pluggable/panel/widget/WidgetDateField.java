package com.itrustcambodia.pluggable.panel.widget;

import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.itrustcambodia.pluggable.panel.InputFeedback;
import com.itrustcambodia.pluggable.validation.controller.FieldController;
import com.itrustcambodia.pluggable.validator.DateValidator;

public class WidgetDateField extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7060419016983447006L;

    private FieldController widget;

    private TextField<String> textField;

    public WidgetDateField(String id, Map<String, Object> object, FieldController widget, Map<String, Object> components) {
        super(id);
        this.widget = widget;

        Label label = new Label("label", this.widget.getTextField().label());
        add(label);

        this.textField = (TextField<String>) components.get(widget.getName());
        add(this.textField);

        if (this.widget.getTextField().placeholder() == null || "".equals(this.widget.getTextField().placeholder())) {
            this.textField.add(AttributeModifier.replace("placeholder", this.widget.getTextField().pattern()));
        } else {
            this.textField.add(AttributeModifier.replace("placeholder", this.widget.getTextField().placeholder()));
        }
        this.textField.add(new DateValidator(this.widget.getTextField().pattern()));

        this.textField.setLabel(new Model<String>(this.widget.getTextField().label()));
        if (this.widget.getNotNull() != null) {
            this.textField.setRequired(true);
        }

        InputFeedback feedback = new InputFeedback("feedback", this.textField);
        add(feedback);

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        StringBuffer jsDate = new StringBuffer();
        jsDate.append("var _" + this.textField.getMarkupId(true) + " = new Pikaday({");
        jsDate.append("field: document.getElementById('" + this.textField.getMarkupId(true) + "'),");
        jsDate.append("format: '" + this.widget.getTextField().pattern().replaceAll("y", "Y").replaceAll("d", "D") + "',");
        jsDate.append("onSelect: function() {");
        // js.append("console.log(this.getMoment().format('Do MMMM YYYY'));");
        jsDate.append("}");
        jsDate.append("})");

        response.render(OnDomReadyHeaderItem.forScript(jsDate.toString()));
    }
}
