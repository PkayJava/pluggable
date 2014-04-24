package com.angkorteam.pluggable.framework.panel.menu;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

import com.angkorteam.pluggable.framework.behaviour.ActiveMenuBehaviour;
import com.angkorteam.pluggable.framework.core.Menu;

/**
 * @author Socheat KHAUV
 */
public class ItemLinkPanel extends Panel {

    private Menu menu;

    /**
     * 
     */
    private static final long serialVersionUID = -7680231749431069387L;

    public ItemLinkPanel(String id, Menu menu) {
        super(id);
        this.menu = menu;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        WebMarkupContainer item = new WebMarkupContainer("item");
        BookmarkablePageLink<Void> page = new BookmarkablePageLink<Void>("page", menu.getPage(), menu.getParameters());
        Label label = new Label("label", menu.getLabel());
        item.add(new ActiveMenuBehaviour(menu));
        page.add(label);
        item.add(page);
        add(item);
    }

}
