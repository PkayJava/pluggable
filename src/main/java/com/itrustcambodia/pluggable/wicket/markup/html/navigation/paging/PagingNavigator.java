package com.itrustcambodia.pluggable.wicket.markup.html.navigation.paging;

import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;

public class PagingNavigator extends org.apache.wicket.markup.html.navigation.paging.PagingNavigator {

    /**
     * 
     */
    private static final long serialVersionUID = -7091600049473723084L;

    public PagingNavigator(String id, IPageable pageable, IPagingLabelProvider labelProvider) {
        super(id, pageable, labelProvider);
    }

    public PagingNavigator(String id, IPageable pageable) {
        super(id, pageable);
    }

}
