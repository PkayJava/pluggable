package com.itrustcambodia.pluggable.panel;

import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import com.itrustcambodia.pluggable.form.ObjectAPIForm;

/**
 * @author Socheat KHAUV
 */
public class FormAPIPanel extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 9070699867881487891L;

    private List<ObjectAPIForm> objectAPIForms;

    public FormAPIPanel(String id, List<ObjectAPIForm> objectAPIForms) {
        super(id);
        this.objectAPIForms = objectAPIForms;

        WebMarkupContainer objectAccordion = new WebMarkupContainer("objectAccordion");
        final String objectAccordionId = objectAccordion.getMarkupId(true);
        add(objectAccordion);
        ListView<ObjectAPIForm> objectItems = new ListView<ObjectAPIForm>("objectItems", this.objectAPIForms) {

            private static final long serialVersionUID = -3813631055862650473L;

            @Override
            protected void populateItem(ListItem<ObjectAPIForm> item) {
                WebMarkupContainer body = new WebMarkupContainer("body");
                String bodyId = body.getMarkupId(true);
                body.setOutputMarkupId(true);
                item.add(body);

                Label deprecated = new Label("deprecated", "Deprecated");
                deprecated.setVisible(item.getModelObject().isDeprecated());
                item.add(deprecated);

                Label head = new Label("head", item.getModelObject().getName());
                head.add(AttributeModifier.replace("data-parent", "#" + objectAccordionId));
                head.add(AttributeModifier.replace("href", "#" + bodyId));
                item.add(head);

                ListView<List<Map<String, String>>> fields = new ListView<List<Map<String, String>>>("fields", item.getModelObject().getFields()) {

                    private static final long serialVersionUID = 6006820023454472328L;

                    @Override
                    protected void populateItem(ListItem<List<Map<String, String>>> item) {
                        ListView<Map<String, String>> field = new ListView<Map<String, String>>("field", item.getModelObject()) {

                            private static final long serialVersionUID = 6222259515631199751L;

                            @Override
                            protected void populateItem(ListItem<Map<String, String>> item) {
                                Label name = new Label("name", item.getModelObject().get("name"));
                                item.add(name);
                                name.setVisible(item.getModelObject().get("name") != null && !"".equals(item.getModelObject().get("name")));

                                Label value = new Label("value", item.getModelObject().get("value"));
                                item.add(value);
                            }
                        };
                        item.add(field);
                    }
                };
                fields.setVisible(item.getModelObject().getFields() != null && !item.getModelObject().getFields().isEmpty());
                body.add(fields);
            }
        };
        objectAccordion.add(objectItems);
    }

}
