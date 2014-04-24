package com.angkorteam.pluggable.wicket.extensions.markup.html.repeater.data.table;

import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;

import com.angkorteam.pluggable.wicket.markup.html.navigation.paging.PagingNavigator;

/**
 * @author Socheat KHAUV
 */
public class NavigationToolbar extends org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar {

    /**
     * 
     */
    private static final long serialVersionUID = 1057961184097803486L;

    public NavigationToolbar(final DataTable<?, ?> table) {
        super(table);
    }

    @Override
    protected PagingNavigator newPagingNavigator(String navigatorId, DataTable<?, ?> table) {
        return new PagingNavigator(navigatorId, table);
    }

}
