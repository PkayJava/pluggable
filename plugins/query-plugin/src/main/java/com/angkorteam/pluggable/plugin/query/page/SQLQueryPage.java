package com.angkorteam.pluggable.plugin.query.page;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.springframework.jdbc.BadSqlGrammarException;

import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.core.Menu;
import com.angkorteam.pluggable.framework.core.Mount;
import com.angkorteam.pluggable.framework.layout.AbstractLayout;
import com.angkorteam.pluggable.framework.page.WebPage;
import com.angkorteam.pluggable.framework.wicket.authroles.Role;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.Roles;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@AuthorizeInstantiation(roles = { @Role(name = "ROLE_QUERY_PLUGIN_PAGE_SQL_QUERY", description = "Role Access Query Plugin SQLQuery Page") })
@Mount("/query/exec")
public class SQLQueryPage extends WebPage {

    /**
     * 
     */
    private static final long serialVersionUID = -7163021630402320758L;

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private String query;

    private List<Map<String, Object>> result;

    private List<String> header;

    private ListView<Map<String, Object>> table;

    private ListView<String> tableHeader;

    public SQLQueryPage() {
        result = new ArrayList<Map<String, Object>>();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        AbstractLayout layout = requestLayout("layout");
        add(layout);

        Form<Void> queryForm = new Form<Void>("queryForm");
        layout.add(queryForm);

        TextArea<String> query = new TextArea<String>("query", new PropertyModel<String>(this, "query"));
        query.setRequired(true);
        queryForm.add(query);

        Button execute = new Button("execute") {

            private static final long serialVersionUID = -5949140457411267920L;

            @Override
            public void onSubmit() {
                executeClick();
            }
        };
        queryForm.add(execute);

        table = new ListView<Map<String, Object>>("table", new PropertyModel<List<Map<String, Object>>>(this, "result")) {

            private static final long serialVersionUID = 7549058662284361134L;

            @Override
            protected void populateItem(ListItem<Map<String, Object>> item) {
                List<String> cells = new ArrayList<String>();
                for (Object cell : item.getModelObject().values()) {
                    if (cell != null) {
                        if (cell instanceof String) {
                            String tmps = (String) cell;
                            if (tmps.length() > 255) {
                                tmps = tmps.substring(0, 252) + "...";
                            }
                            cells.add(tmps);
                        } else if (cell instanceof Number) {
                            cells.add(String.valueOf((Number) cell));
                        } else if (cell instanceof Date) {
                            cells.add(String.valueOf(DATE_FORMAT.format((Date) cell)));
                        } else if (cell instanceof Boolean) {
                            cells.add(String.valueOf((Boolean) cell));
                        }
                    } else {
                        cells.add("");
                    }
                }
                ListView<String> body = new ListView<String>("body", cells) {

                    private static final long serialVersionUID = -6963456811385417005L;

                    @Override
                    protected void populateItem(ListItem<String> item) {
                        Label cell = new Label("cell", item.getModelObject());
                        item.add(cell);
                    }
                };
                item.add(body);
            }
        };
        layout.add(table);
        table.setVisible(result != null && !result.isEmpty());

        tableHeader = new ListView<String>("header", new PropertyModel<List<String>>(this, "header")) {

            private static final long serialVersionUID = -5410144642546780948L;

            @Override
            protected void populateItem(ListItem<String> item) {
                Label cell = new Label("cell", item.getModelObject());
                item.add(cell);
            }
        };
        layout.add(tableHeader);

    }

    public void executeClick() {

        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        try {
            if (query.toLowerCase().startsWith("show databases") || query.toLowerCase().startsWith("drop database")) {
                return;
            }
            if (query.toLowerCase().startsWith("select") || query.toLowerCase().startsWith("show ") || query.toLowerCase().startsWith("desc ")) {
                this.result = application.getJdbcTemplate().queryForList(query);
            } else {
                application.getJdbcTemplate().execute(query);
                this.result = null;
            }
            table.setVisible(result != null && !result.isEmpty());
        } catch (BadSqlGrammarException e) {
            setResponsePage(SQLQueryPage.class);
        }
    }

    @Override
    public String getPageTitle() {
        return "SQL Query";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return null;
    }

    public List<Map<String, Object>> getResult() {
        return result;
    }

    public List<String> getHeader() {
        header = new ArrayList<String>();
        if (this.result != null && !this.result.isEmpty()) {
            Map<String, Object> tmp = this.result.get(0);
            if (!tmp.isEmpty()) {
                for (String header : tmp.keySet()) {
                    this.header.add(header);
                }
            }

        }
        return this.header;
    }

}
