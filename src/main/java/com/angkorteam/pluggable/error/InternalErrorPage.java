package com.angkorteam.pluggable.error;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.request.http.WebResponse;

import com.angkorteam.pluggable.core.Menu;
import com.angkorteam.pluggable.core.Mount;
import com.angkorteam.pluggable.layout.ErrorLayout;
import com.angkorteam.pluggable.wicket.authroles.authorization.strategies.role.Roles;

/**
 * @author Socheat KHAUV
 */
@Mount("/x")
public class InternalErrorPage extends AbstractErrorPage {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     */
    public InternalErrorPage() {
        ErrorLayout layout = new ErrorLayout("layout");
        add(layout);
        layout.add(homePageLink("homePageLink"));
    }

    @Override
    protected void setHeaders(final WebResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    @Override
    public String getPageTitle() {
        return "Internal Error";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return null;
    }
}