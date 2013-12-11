package com.itrustcambodia.pluggable.form;

import java.util.Date;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

public class DateTextField extends TextField<Date> {

    /**
     * 
     */
    private static final long serialVersionUID = -8675034578928681023L;

    private String format;

    public DateTextField(String id, String format) {
        super(id);
        this.format = format;
        initializeInterceptor();
    }

    public DateTextField(String id, Class<Date> type, String format) {
        super(id, type);
        this.format = format;
        initializeInterceptor();
    }

    public DateTextField(String id, IModel<Date> model, String format) {
        super(id, model);
        this.format = format;
        initializeInterceptor();
    }

    public DateTextField(String id, IModel<Date> model, Class<Date> type,
            String format) {
        super(id, model, type);
        this.format = format;
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
        jsDate.append("format: '"
                + this.format.replaceAll("y", "Y").replaceAll("d", "D") + "',");
        jsDate.append("onSelect: function() {");
        jsDate.append("}");
        jsDate.append("})");

        response.render(OnDomReadyHeaderItem.forScript(jsDate.toString()));
    }

}
