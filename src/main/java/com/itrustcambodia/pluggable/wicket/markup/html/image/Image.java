package com.itrustcambodia.pluggable.wicket.markup.html.image;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;

/**
 * @author Socheat KHAUV
 */
public class Image extends WebComponent {

    /**
     * 
     */
    private static final long serialVersionUID = -3488158078494611993L;

    private String src;

    public Image(String id, String src) {
        super(id);
        this.src = src;
    }

    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        checkComponentTag(tag, "img");
        tag.put("src", src);
    }

}
