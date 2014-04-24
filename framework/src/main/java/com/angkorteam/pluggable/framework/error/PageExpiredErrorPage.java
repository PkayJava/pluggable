package com.angkorteam.pluggable.framework.error;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.request.http.WebResponse;

import com.angkorteam.pluggable.framework.core.Menu;
import com.angkorteam.pluggable.framework.core.Mount;
import com.angkorteam.pluggable.framework.layout.ErrorLayout;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.Roles;

/**
 * @author Socheat KHAUV
 */
@Mount("/l")
public class PageExpiredErrorPage extends AbstractErrorPage {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public PageExpiredErrorPage() {
        ErrorLayout layout = new ErrorLayout("layout");
        add(layout);
        layout.add(homePageLink("homePageLink"));
    }

    @Override
    protected void setHeaders(final WebResponse response) {
        response.setStatus(HttpServletResponse.SC_GONE);
    }

    @Override
    public String getPageTitle() {
        return "Page Expired";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return null;
    }
}