package com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.IModel;

/**
 * @author Socheat KHAUV
 */
public class DefaultDataTable<T, S> extends DataTable<T, S> {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * 
     * @param id
     *            component id
     * @param columns
     *            list of columns
     * @param dataProvider
     *            data provider
     * @param rowsPerPage
     *            number of rows per page
     */
    public DefaultDataTable(final String id, final List<? extends IColumn<T, S>> columns, final ISortableDataProvider<T, S> dataProvider, final int rowsPerPage) {
        super(id, columns, dataProvider, rowsPerPage);

        addTopToolbar(new HeadersToolbar<S>(this, dataProvider));
        addBottomToolbar(new NoRecordsToolbar(this));
        addBottomToolbar(new NavigationToolbar(this));
    }

    @Override
    protected Item<T> newRowItem(final String id, final int index, final IModel<T> model) {
        return new OddEvenItem<T>(id, index, model);
    }

}