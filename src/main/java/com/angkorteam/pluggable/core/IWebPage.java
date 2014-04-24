package com.angkorteam.pluggable.core;

import java.util.List;

import org.apache.wicket.markup.html.border.Border;

import com.angkorteam.pluggable.wicket.authroles.authorization.strategies.role.Roles;

/**
 * @author Socheat KHAUV
 */
public interface IWebPage {

    String getTitle();

    List<Menu> getPageMenus(Roles roles);

    Border requestLayout(String id);
}
