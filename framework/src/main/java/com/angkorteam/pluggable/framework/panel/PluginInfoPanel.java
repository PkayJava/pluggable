package com.angkorteam.pluggable.framework.panel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

/**
 * @author Socheat KHAUV
 */
public class PluginInfoPanel extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 8738735468505274275L;

    private String name;

    private String identity;

    public PluginInfoPanel(String id, String name, String identity) {
        super(id);
        this.name = name;
        this.identity = identity;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Label name = new Label("name", this.name);
        add(name);

        Label identity = new Label("identity", this.identity);

        add(identity);
    }
}
