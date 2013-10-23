package com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.filter;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;

/**
 * @author Socheat KHAUV
 */
public class FilterToolbar extends org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar {

    /**
     * 
     */
    private static final long serialVersionUID = 7585327710822016088L;

    public <T, S> FilterToolbar(DataTable<T, S> table, FilterForm<T> form, IFilterStateLocator<T> stateLocator) {
        super(table, form, stateLocator);
    }

}
