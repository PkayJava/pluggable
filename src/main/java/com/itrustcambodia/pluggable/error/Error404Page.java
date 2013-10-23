package com.itrustcambodia.pluggable.error;

import java.util.List;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.layout.ErrorLayout;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;

/**
 * @author Socheat KHAUV
 */
@Mount("/404")
public class Error404Page extends AbstractErrorPage {

    /**
     * 
     */
    private static final long serialVersionUID = 8988290208275990421L;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        ErrorLayout layout = new ErrorLayout("layout");
        add(layout);

        layout.add(new BookmarkablePageLink<Void>("homePageLink", getApplication().getHomePage()));
    }

    @Override
    public String getPageTitle() {
        return "Error Page";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        // TODO Auto-generated method stub
        return null;
    }

}
