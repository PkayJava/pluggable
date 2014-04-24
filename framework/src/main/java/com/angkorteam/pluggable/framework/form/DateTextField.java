package com.angkorteam.pluggable.framework.form;

import java.util.Date;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;

public class DateTextField extends
        org.apache.wicket.extensions.markup.html.form.DateTextField {

    /**
     * 
     */
    private static final long serialVersionUID = -8675034578928681023L;

    private String format;

    public DateTextField(String id, String datePattern) {
        super(id, datePattern);
        this.format = datePattern.replaceAll("y", "Y").replaceAll("d", "D");
        initializeInterceptor();
    }

    public DateTextField(String id, IModel<Date> model, String datePattern) {
        super(id, model, datePattern);
        this.format = datePattern.replaceAll("y", "Y").replaceAll("d", "D");
        initializeInterceptor();
    }

    private void initializeInterceptor() {
        if (this.format == null || this.format.equals("")) {
            throw new NullPointerException("format can't not be null or empty");
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        String markupId = getMarkupId(true);

        StringBuffer jsDate = new StringBuffer();
        jsDate.append("var _" + markupId + " = new Pikaday({");
        jsDate.append("field: document.getElementById('" + markupId + "'),");
        jsDate.append("format: '" + this.format + "',");
        jsDate.append("onSelect: function() {");
        jsDate.append("}");
        jsDate.append("})");

        response.render(OnDomReadyHeaderItem.forScript(jsDate.toString()));
    }

}
