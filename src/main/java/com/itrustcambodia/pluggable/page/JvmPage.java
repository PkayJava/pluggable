package com.itrustcambodia.pluggable.page;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.utilities.FrameworkUtilities;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@Mount("/jv")
@AuthorizeInstantiation(roles = { @com.itrustcambodia.pluggable.wicket.authroles.Role(name = "ROLE_PAGE_JVM", description = "Access JVM Page") })
public class JvmPage extends WebPage {

    private static final long serialVersionUID = -8711509323120746132L;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        Border layout = requestLayout("layout");
        add(layout);

        List<Map<String, String>> envs = new ArrayList<Map<String, String>>();
        for (String name : System.getenv().keySet()) {
            Map<String, String> env = new HashMap<String, String>();
            env.put("name", name);
            env.put("value", System.getenv().get(name));
            envs.add(env);
        }

        ListView<Map<String, String>> envtable = new ListView<Map<String, String>>("envtable", envs) {

            private static final long serialVersionUID = -6077251552007209672L;

            @Override
            protected void populateItem(ListItem<Map<String, String>> item) {
                Label name = new Label("name", item.getModelObject().get("name"));
                item.add(name);
                Label value = new Label("value", item.getModelObject().get("value"));
                item.add(value);
            }
        };
        layout.add(envtable);

        List<Map<String, String>> systems = new ArrayList<Map<String, String>>();

        Enumeration<Object> names = System.getProperties().keys();
        while (names.hasMoreElements()) {
            Object name = names.nextElement();
            Map<String, String> system = new HashMap<String, String>();
            system.put("name", String.valueOf(name));
            system.put("value", System.getProperty((String) name));
            systems.add(system);
        }

        ListView<Map<String, String>> systemtable = new ListView<Map<String, String>>("systemtable", systems) {

            private static final long serialVersionUID = -6077251552007209672L;

            @Override
            protected void populateItem(ListItem<Map<String, String>> item) {
                Label name = new Label("name", item.getModelObject().get("name"));
                item.add(name);
                Label value = new Label("value", item.getModelObject().get("value"));
                item.add(value);
            }
        };
        layout.add(systemtable);
    }

    @Override
    public String getPageTitle() {
        return "JVM";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        return FrameworkUtilities.getSecurityMenu(application, roles).getChildren();
    }

}
