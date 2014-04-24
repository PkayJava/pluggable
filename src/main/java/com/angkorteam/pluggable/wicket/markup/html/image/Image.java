package com.angkorteam.pluggable.wicket.markup.html.image;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * @author Socheat KHAUV
 */
public class Image extends WebComponent {

    /**
     * 
     */
    private static final long serialVersionUID = -3488158078494611993L;

    public Image(String id, IModel<String> model) {
        super(id, model);
    }

    public Image(String id, String href) {
        super(id, new Model<String>(href));
    }

    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        checkComponentTag(tag, "img");
        tag.put("src", (String) getDefaultModelObject());
    }

}
