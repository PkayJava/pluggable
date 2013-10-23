package com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.filter;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

public class ChoiceFilteredPropertyColumn<T, Y, S> extends org.apache.wicket.extensions.markup.html.repeater.data.table.filter.ChoiceFilteredPropertyColumn<T, Y, S> {

    /**
     * 
     */
    private static final long serialVersionUID = -3765814653522611602L;

    public ChoiceFilteredPropertyColumn(IModel<String> displayModel, S sortProperty, String propertyExpression, IModel<List<? extends Y>> filterChoices) {
        super(displayModel, sortProperty, propertyExpression, filterChoices);
    }

    public ChoiceFilteredPropertyColumn(IModel<String> displayModel, String propertyExpression, IModel<List<? extends Y>> filterChoices) {
        super(displayModel, propertyExpression, filterChoices);
    }

    @Override
    public Component getFilter(final String componentId, final FilterForm<?> form) {
        ChoiceFilter<Y> filter = new ChoiceFilter<Y>(componentId, getFilterModel(form), form, getFilterChoices(), enableAutoSubmit());

        IChoiceRenderer<Y> renderer = getChoiceRenderer();
        if (renderer != null) {
            filter.getChoice().setChoiceRenderer(renderer);
        }
        return filter;
    }
}
