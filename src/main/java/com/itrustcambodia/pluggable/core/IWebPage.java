package com.itrustcambodia.pluggable.core;

import java.util.List;

import org.apache.wicket.markup.html.border.Border;

import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;

public interface IWebPage {

    String getTitle();

    List<Menu> getPageMenus(Roles roles);

    Border requestLayout(String id);
}
