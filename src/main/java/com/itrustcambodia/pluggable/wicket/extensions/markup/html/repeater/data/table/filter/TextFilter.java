package com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.filter;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.model.IModel;

/**
 * @author Socheat KHAUV
 */
public class TextFilter<T> extends org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter<T> {

    /**
     * 
     */
    private static final long serialVersionUID = -6986515621490722080L;

    public TextFilter(String id, IModel<T> model, FilterForm<?> form) {
        super(id, model, form);
    }

}
