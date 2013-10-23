package com.itrustcambodia.pluggable.behaviour;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;

import com.itrustcambodia.pluggable.core.Menu;

public class ActiveMenuBehaviour extends Behavior {

    private Menu menu;

    public ActiveMenuBehaviour(Menu menu) {
        this.menu = menu;
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        if (isEnabled(component)) {
            if (menu.getPage().getName().equals(component.getPage().getClass().getName())) {
                String css = tag.getAttribute("class");
                if (org.apache.commons.lang3.StringUtils.isBlank(css)) {
                    css = "active";
                } else {
                    css = css + " active";
                }
                tag.getAttributes().put("class", css);
            }
        }
    }

    /**
     * 
     */
    private static final long serialVersionUID = -5780091688846648895L;

}
