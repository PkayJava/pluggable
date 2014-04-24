package com.angkorteam.pluggable.panel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
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

import com.angkorteam.pluggable.validation.controller.LinkController;
import com.angkorteam.pluggable.validation.controller.Navigation;
import com.angkorteam.pluggable.widget.Link;

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
        List<LinkController> links = new ArrayList<LinkController>();
        for (Method method : ReflectionUtils.getAllMethods(this.getClass())) {
            if (method.getAnnotation(Link.class) != null) {
                LinkController link = new LinkController();
                link.setMethod(method.getName());
                link.setLabel(method.getAnnotation(Link.class).label());
                link.setOrder(method.getAnnotation(Link.class).order());
                links.add(link);
            }
        }
        Collections.sort(links);

        for (LinkController model : links) {
            org.apache.wicket.markup.html.link.Link<String> link = new org.apache.wicket.markup.html.link.Link<String>("link", Model.<String> of(model.getMethod())) {

                /**
                 * 
                 */
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick() {
                    linkClick(getModelObject());
                }
            };

            Label label = new Label("label", model.getLabel());
            link.add(label);
            KnownPanel.this.links.put(model.getMethod(), link);
        }
        ListView<LinkController> listView = new ListView<LinkController>("links", links) {

            /**
             * 
             */
            private static final long serialVersionUID = -9162943532693333702L;

            @Override
            protected void populateItem(ListItem<LinkController> item) {
                item.add(KnownPanel.this.links.get(item.getModelObject().getMethod()));
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
