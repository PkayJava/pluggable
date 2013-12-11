package com.itrustcambodia.pluggable.wicket.markup.html.image;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;

/**
 * @author Socheat KHAUV
 */
public class Image extends WebComponent {

    /**
     * 
     */
    private static final long serialVersionUID = -3488158078494611993L;

    private IModel<String> model;

    public Image(String id, IModel<String> model) {
        super(id);
        this.model = model;
    }

    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        checkComponentTag(tag, "img");
        tag.put("src", model.getObject());
    }

}
