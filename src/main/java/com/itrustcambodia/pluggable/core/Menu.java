package com.itrustcambodia.pluggable.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.itrustcambodia.pluggable.page.WebPage;

public class Menu implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4877492578751743446L;

    private String label;

    private Class<? extends WebPage> page;

    private List<Menu> children = null;

    private int type;

    private int badge;

    private PageParameters parameters;

    public String getLabel() {
        return label;
    }

    public Class<? extends WebPage> getPage() {
        return page;
    }

    public List<Menu> getChildren() {
        return children;
    }

    public int getType() {
        return type;
    }

    public PageParameters getParameters() {
        return parameters;
    }

    private Menu() {
    }

    public static Menu parentMenu(String label) {
        return parentMenu(label, new ArrayList<Menu>());
    }

    public static Menu parentMenu(String label, List<Menu> children) {
        Menu menu = new Menu();
        menu.label = label;
        menu.children = children;
        menu.type = Type.PARENT;
        return menu;
    }

    public static Menu linkMenu(String label, Class<? extends WebPage> page) {
        return linkMenu(label, page, null);
    }

    public static Menu linkMenu(String label, Class<? extends WebPage> page, int badge) {
        return linkMenu(label, page, null, badge);
    }

    public static Menu linkMenu(String label, Class<? extends WebPage> page, PageParameters parameters) {
        return linkMenu(label, page, parameters, 0);
    }

    public static Menu linkMenu(String label, Class<? extends WebPage> page, PageParameters parameters, int badge) {
        Menu menu = new Menu();
        menu.label = label;
        menu.page = page;
        menu.badge = badge;
        menu.parameters = parameters;
        menu.type = Type.LINK;
        return menu;
    }

    public static Menu headMenu(String label) {
        Menu menu = new Menu();
        menu.label = label;
        menu.type = Type.HEAD;
        return menu;
    }

    public static Menu labelMenu(String label) {
        Menu menu = new Menu();
        menu.label = label;
        menu.type = Type.LABEL;
        return menu;
    }

    public static Menu dividerMenu() {
        Menu menu = new Menu();
        menu.type = Type.DIVIDER;
        return menu;
    }

    public abstract static class Type {
        public static final int PARENT = 1;
        public static final int LINK = 2;
        public static final int HEAD = 3;
        public static final int DIVIDER = 4;
        public static final int LABEL = 5;
    }

    public int getBadge() {
        return badge;
    }

    public void setBadge(int badge) {
        this.badge = badge;
    }

}
