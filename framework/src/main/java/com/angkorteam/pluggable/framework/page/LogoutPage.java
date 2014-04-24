package com.angkorteam.pluggable.framework.page;

import java.util.List;

import com.angkorteam.pluggable.framework.core.Menu;
import com.angkorteam.pluggable.framework.core.Mount;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.Roles;

/**
 * @author Socheat KHAUV
 */
@Mount("/o")
public final class LogoutPage extends WebPage {

    /**
     * 
     */
    private static final long serialVersionUID = -2208918879890316947L;

    public LogoutPage() {
        getSession().invalidate();
        setResponsePage(getApplication().getHomePage());
    }

    @Override
    public String getPageTitle() {
        return "Logout";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return null;
    }

}
