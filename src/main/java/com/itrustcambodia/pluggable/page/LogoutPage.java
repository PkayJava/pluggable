package com.itrustcambodia.pluggable.page;

import java.util.List;

import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;

/**
 * @author Socheat KHAUV
 */
@Mount("/o")
public class LogoutPage extends WebPage {

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
