package com.angkorteam.pluggable.wicket.extensions.markup.html.repeater.data.table.filter;

import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.MapModel;

import com.angkorteam.pluggable.wicket.model.MapPropertyModel;

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

    @SuppressWarnings("unchecked")
    protected IModel<F> getFilterModel(final FilterForm<?> form) {
        if (form.getDefaultModel().getObject() instanceof Map) {
            return new MapPropertyModel<F>((Map<String, F>) form.getDefaultModel().getObject(), getPropertyExpression());
        } else {
            return new PropertyModel<F>(form.getDefaultModel(), getPropertyExpression());
        }
    }

    @Override
    public IModel<Object> getDataModel(IModel<T> rowModel) {
        if (rowModel instanceof MapModel) {
            @SuppressWarnings("unchecked")
            MapPropertyModel<Object> propertyModel = new MapPropertyModel<Object>((Map<String, Object>) rowModel.getObject(), getPropertyExpression());
            return propertyModel;
        } else {
            return super.getDataModel(rowModel);
        }
    }

}
