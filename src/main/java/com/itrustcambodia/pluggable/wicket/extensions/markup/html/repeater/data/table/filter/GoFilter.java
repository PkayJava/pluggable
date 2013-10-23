package com.itrustcambodia.pluggable.wicket.extensions.markup.html.repeater.data.table.filter;

import org.apache.wicket.model.IModel;

public class GoFilter extends org.apache.wicket.extensions.markup.html.repeater.data.table.filter.GoFilter {

    /**
     * 
     */
    private static final long serialVersionUID = 238324093663988353L;

    public GoFilter(String id, IModel<String> goModel) {
        super(id, goModel);
    }

    public GoFilter(String id) {
        super(id);
    }

}
