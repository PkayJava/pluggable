package com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.filter;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.model.IModel;

/**
 * @author Socheat KHAUV
 */
public class GoAndClearFilter extends org.apache.wicket.extensions.markup.html.repeater.data.table.filter.GoAndClearFilter {

    /**
     * 
     */
    private static final long serialVersionUID = -3738049952112592857L;

    public GoAndClearFilter(String id, FilterForm<?> form, IModel<String> goModel, IModel<String> clearModel) {
        super(id, form, goModel, clearModel);
    }

    public GoAndClearFilter(String id, FilterForm<?> form) {
        super(id, form);
    }

    public GoAndClearFilter(String id, IModel<String> goModel, IModel<String> clearModel, Object originalState) {
        super(id, goModel, clearModel, originalState);
    }

}
