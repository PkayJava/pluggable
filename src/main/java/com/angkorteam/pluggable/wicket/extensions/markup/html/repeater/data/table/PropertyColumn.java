package com.angkorteam.pluggable.wicket.extensions.markup.html.repeater.data.table;

import java.util.Map;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.MapModel;

import com.angkorteam.pluggable.wicket.model.MapPropertyModel;

public class PropertyColumn<T, S> extends org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn<T, S> {

    /**
     * 
     */
    private static final long serialVersionUID = 9146894050362889391L;

    public PropertyColumn(IModel<String> displayModel, S sortProperty, String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
    }

    public PropertyColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
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
