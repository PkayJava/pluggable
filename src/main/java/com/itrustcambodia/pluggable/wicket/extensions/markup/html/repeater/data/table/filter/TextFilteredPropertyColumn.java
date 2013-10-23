package com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.filter;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.model.IModel;

/**
 * @author Socheat KHAUV
 */
public class TextFilteredPropertyColumn<T, F, S> extends org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn<T, F, S> {

    /**
     * 
     */
    private static final long serialVersionUID = 1925834533298181780L;

    public TextFilteredPropertyColumn(IModel<String> displayModel, S sortProperty, String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
    }

    public TextFilteredPropertyColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        return new TextFilter<F>(componentId, getFilterModel(form), form);
    }

}
