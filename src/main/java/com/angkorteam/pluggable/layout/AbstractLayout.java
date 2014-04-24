package com.angkorteam.pluggable.layout;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.model.IModel;

/**
 * @author Socheat KHAUV
 */
public abstract class AbstractLayout extends Border {

    /**
     * 
     */
    private static final long serialVersionUID = -3293930125616091104L;

    public AbstractLayout(String id) {
        super(id);
    }

    public AbstractLayout(String id, IModel<?> model) {
        super(id, model);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
    }
}
