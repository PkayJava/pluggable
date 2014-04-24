package com.angkorteam.pluggable.panel.menu;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import com.angkorteam.pluggable.core.Menu;

/**
 * @author Socheat KHAUV
 */
public class ItemParentPanel extends Panel {

    private Menu menu;

    public ItemParentPanel(String id, Menu menu) {
        super(id);
        this.menu = menu;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Label label = new Label("label", menu.getLabel());
        add(label);

        ListView<Menu> navItems = new ListView<Menu>("navItems", menu.getChildren()) {

            private static final long serialVersionUID = 6858380830903152703L;

            @Override
            protected void populateItem(ListItem<Menu> item) {
                if (item.getModelObject().getType() == Menu.Type.LINK) {
                    ItemLinkPanel itemLinkPanel = new ItemLinkPanel("navItem", item.getModelObject());
                    item.add(itemLinkPanel);
                }
                if (item.getModelObject().getType() == Menu.Type.PARENT) {
                    ItemParentPanel itemParentPanel = new ItemParentPanel("navItem", item.getModelObject());
                    item.add(itemParentPanel);
                }
                if (item.getModelObject().getType() == Menu.Type.DIVIDER) {
                    ItemDividerPanel itemDividerPanel = new ItemDividerPanel("navItem");
                    item.add(itemDividerPanel);
                }
                if (item.getModelObject().getType() == Menu.Type.HEAD) {
                    ItemHeadPanel itemHeadPanel = new ItemHeadPanel("navItem", item.getModelObject());
                    item.add(itemHeadPanel);
                }
                if (item.getModelObject().getType() == Menu.Type.LABEL) {
                    ItemLabelPanel itemLabelPanel = new ItemLabelPanel("navItem", item.getModelObject());
                    item.add(itemLabelPanel);
                }
            }
        };
        add(navItems);
    }

    /**
     * 
     */
    private static final long serialVersionUID = -7680231749431069387L;

}
