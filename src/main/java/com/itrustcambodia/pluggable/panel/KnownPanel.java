package com.itrustcambodia.pluggable.panel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.reflections.ReflectionUtils;

import com.itrustcambodia.pluggable.validation.controller.Navigation;
import com.itrustcambodia.pluggable.widget.Link;

public abstract class KnownPanel extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = -6792935004823744015L;

    private Map<String, org.apache.wicket.markup.html.link.Link<?>> links = new HashMap<String, org.apache.wicket.markup.html.link.Link<?>>();

    public KnownPanel(String id) {
        super(id);
        initializeInterceptor();
    }

    public KnownPanel(String id, IModel<?> model) {
        super(id, model);
        initializeInterceptor();
    }

    protected final org.apache.wicket.markup.html.link.Link<?> getLinkComponent(String name) {
        return this.links.get(name);
    }

    @SuppressWarnings("unchecked")
    private void initializeInterceptor() {
        List<Map<String, String>> links = new ArrayList<Map<String, String>>();
        for (Method method : ReflectionUtils.getAllMethods(this.getClass())) {
            if (method.getAnnotation(Link.class) != null) {
                Map<String, String> link = new HashMap<String, String>();
                link.put("method", method.getName());
                link.put("label", method.getAnnotation(Link.class).label());
                links.add(link);
            }
        }

        for (Map<String, String> model : links) {
            org.apache.wicket.markup.html.link.Link<String> link = new org.apache.wicket.markup.html.link.Link<String>("link", Model.<String> of(model.get("method"))) {

                /**
                 * 
                 */
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    linkClick(getModelObject());
                }
            };

            Label label = new Label("label", model.get("label"));
            link.add(label);
            KnownPanel.this.links.put(model.get("method"), link);
        }
        ListView<Map<String, String>> listView = new ListView<Map<String, String>>("links", links) {

            /**
             * 
             */
            private static final long serialVersionUID = -9162943532693333702L;

            @Override
            protected void populateItem(ListItem<Map<String, String>> item) {
                item.add(KnownPanel.this.links.get(item.getModelObject().get("method")));
            }
        };
        add(listView);
    }

    protected final void linkClick(String methodName) {
        try {
            Method method = this.getClass().getMethod(methodName);
            Navigation navigation = (Navigation) method.invoke(this);
            if (navigation != null) {
                if (navigation.getInstance() != null) {
                    setResponsePage(navigation.getInstance());
                }
                if (navigation.getClazz() != null) {
                    if (navigation.getParameters() != null) {
                        setResponsePage(navigation.getClazz(), navigation.getParameters());
                    } else {
                        setResponsePage(navigation.getClazz());
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
