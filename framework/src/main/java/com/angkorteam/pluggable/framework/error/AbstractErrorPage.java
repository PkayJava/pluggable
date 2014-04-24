package com.angkorteam.pluggable.framework.error;

import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.angkorteam.pluggable.framework.page.WebPage;

/**
 * @author Socheat KHAUV
 */
public abstract class AbstractErrorPage extends WebPage {
    private static final long serialVersionUID = 1L;

    protected AbstractErrorPage() {
        super();
    }

    protected AbstractErrorPage(final IModel<?> model) {
        super(model);
    }

    protected AbstractErrorPage(final PageParameters parameters) {
        super(parameters);
    }

    @Override
    public boolean isErrorPage() {
        return true;
    }

    @Override
    public boolean isVersioned() {
        return false;
    }
}