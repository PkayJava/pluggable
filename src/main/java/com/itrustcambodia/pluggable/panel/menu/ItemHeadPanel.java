package com.itrustcambodia.pluggable.panel.menu;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import com.itrustcambodia.pluggable.core.Menu;

public class ItemHeadPanel extends Panel {

    private Menu menu;

    public ItemHeadPanel(String id, Menu menu) {
        super(id);
        this.menu = menu;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Label label = new Label("label", menu.getLabel());
        add(label);
    }

    /**
     * 
     */
    private static final long serialVersionUID = -7680231749431069387L;

}
