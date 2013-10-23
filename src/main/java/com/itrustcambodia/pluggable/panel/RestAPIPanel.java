package com.itrustcambodia.pluggable.panel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

import com.itrustcambodia.pluggable.form.RestAPIForm;
import com.itrustcambodia.pluggable.rest.RequestMethod;

/**
 * @author Socheat KHAUV
 */
public class RestAPIPanel extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 9070699867881487891L;

    private List<RestAPIForm> restAPIForms;

    public RestAPIPanel(String id, List<RestAPIForm> restAPIForms) {
        super(id);
        this.restAPIForms = restAPIForms;

        WebMarkupContainer restAccordion = new WebMarkupContainer("restAccordion");
        final String restAccordionId = restAccordion.getMarkupId(true);
        add(restAccordion);

        ListView<RestAPIForm> restItems = new ListView<RestAPIForm>("restItems", this.restAPIForms) {

            private static final long serialVersionUID = 1438321682217689870L;

            @Override
            protected void populateItem(ListItem<RestAPIForm> item) {
                WebMarkupContainer body = new WebMarkupContainer("body");
                String bodyId = body.getMarkupId(true);
                body.setOutputMarkupId(true);
                item.add(body);

                Label head = new Label("head", item.getModelObject().getPath());
                head.add(AttributeModifier.replace("data-parent", "#" + restAccordionId));
                head.add(AttributeModifier.replace("href", "#" + bodyId));
                item.add(head);

                Label deprecated = new Label("deprecated", "Deprecated");
                deprecated.setVisible(item.getModelObject().isDeprecated());
                item.add(deprecated);

                ListView<RequestMethod> methods = new ListView<RequestMethod>("methods", Arrays.asList(item.getModelObject().getMethod())) {

                    private static final long serialVersionUID = -2832929292740133237L;

                    @Override
                    protected void populateItem(ListItem<RequestMethod> item) {
                        Label method = new Label("method", item.getModelObject().name());
                        if (item.getModelObject() == RequestMethod.GET) {
                            method.add(AttributeModifier.replace("class", "label label-info"));
                        } else if (item.getModelObject() == RequestMethod.POST) {
                            method.add(AttributeModifier.replace("class", "label label-success"));
                        } else if (item.getModelObject() == RequestMethod.PUT) {
                            method.add(AttributeModifier.replace("class", "label label-warning"));
                        } else if (item.getModelObject() == RequestMethod.DELETE) {
                            method.add(AttributeModifier.replace("class", "label label-danger"));
                        } else {
                            method.add(AttributeModifier.replace("class", "label label-default"));
                        }
                        item.add(method);
                    }
                };
                methods.setVisible(item.getModelObject().getMethod() != null);
                item.add(methods);

                Label path = new Label("path", item.getModelObject().getPath());
                body.add(path);

                Label description = new Label("description", item.getModelObject().getDescription());
                body.add(description);

                ListView<RequestMethod> method1 = new ListView<RequestMethod>("method1", Arrays.asList(item.getModelObject().getMethod())) {

                    private static final long serialVersionUID = 2517437141389219449L;

                    @Override
                    protected void populateItem(ListItem<RequestMethod> item) {
                        Label method = new Label("method", item.getModelObject().name());
                        if (item.getModelObject() == RequestMethod.GET) {
                            method.add(AttributeModifier.replace("class", "label label-info"));
                        } else if (item.getModelObject() == RequestMethod.POST) {
                            method.add(AttributeModifier.replace("class", "label label-success"));
                        } else if (item.getModelObject() == RequestMethod.PUT) {
                            method.add(AttributeModifier.replace("class", "label label-warning"));
                        } else if (item.getModelObject() == RequestMethod.DELETE) {
                            method.add(AttributeModifier.replace("class", "label label-danger"));
                        } else {
                            method.add(AttributeModifier.replace("class", "label label-default"));
                        }
                        item.add(method);
                    }
                };
                body.add(method1);
                method1.setVisible(item.getModelObject().getMethod() != null);

                ListView<String> roles = new ListView<String>("roles", Arrays.asList(item.getModelObject().getRoles())) {

                    private static final long serialVersionUID = -1493201333747747266L;

                    @Override
                    protected void populateItem(ListItem<String> item) {
                        Label method = new Label("role", item.getModelObject());
                        item.add(method);
                    }
                };
                roles.setVisible(item.getModelObject().getRoles() != null && item.getModelObject().getRoles().length != 0);
                body.add(roles);

                ListView<List<Map<String, String>>> urlParameters = new ListView<List<Map<String, String>>>("urlParameters", item.getModelObject().getUrlParameters()) {

                    private static final long serialVersionUID = 678059867689378626L;

                    @Override
                    protected void populateItem(ListItem<List<Map<String, String>>> item) {
                        ListView<Map<String, String>> urlParameter = new ListView<Map<String, String>>("urlParameter", item.getModelObject()) {

                            private static final long serialVersionUID = 3849467973532369190L;

                            @Override
                            protected void populateItem(ListItem<Map<String, String>> item) {
                                Label name = new Label("name", item.getModelObject().get("name"));
                                item.add(name);
                                name.setVisible(item.getModelObject().get("name") != null && !"".equals(item.getModelObject().get("name")));

                                Label value = new Label("value", item.getModelObject().get("value"));
                                item.add(value);
                            }
                        };
                        item.add(urlParameter);
                    }
                };
                urlParameters.setVisible(item.getModelObject().getUrlParameters() != null && !item.getModelObject().getUrlParameters().isEmpty());
                body.add(urlParameters);

                ListView<List<Map<String, String>>> formParameters = new ListView<List<Map<String, String>>>("formParameters", item.getModelObject().getFormParameters()) {

                    private static final long serialVersionUID = -1304228070319726021L;

                    @Override
                    protected void populateItem(ListItem<List<Map<String, String>>> item) {
                        ListView<Map<String, String>> formParameter = new ListView<Map<String, String>>("formParameter", item.getModelObject()) {

                            private static final long serialVersionUID = 4227868761076499821L;

                            @Override
                            protected void populateItem(ListItem<Map<String, String>> item) {
                                Label name = new Label("name", item.getModelObject().get("name"));
                                item.add(name);
                                name.setVisible(item.getModelObject().get("name") != null && !"".equals(item.getModelObject().get("name")));

                                Label value = new Label("value", item.getModelObject().get("value"));
                                item.add(value);
                            }
                        };
                        item.add(formParameter);
                    }
                };
                formParameters.setVisible(item.getModelObject().getFormParameters() != null && !item.getModelObject().getFormParameters().isEmpty());
                body.add(formParameters);

                ListView<Map<String, String>> headers = new ListView<Map<String, String>>("headers", item.getModelObject().getHeaders()) {

                    private static final long serialVersionUID = 6581707044040442920L;

                    @Override
                    protected void populateItem(ListItem<Map<String, String>> item) {
                        Label name = new Label("name", item.getModelObject().get("name"));
                        item.add(name);

                        Label description = new Label("description", item.getModelObject().get("description"));
                        item.add(description);
                    }
                };
                headers.setVisible(item.getModelObject().getHeaders() != null && !item.getModelObject().getHeaders().isEmpty());
                body.add(headers);

                ListView<Map<String, String>> errors = new ListView<Map<String, String>>("errors", item.getModelObject().getErrors()) {

                    private static final long serialVersionUID = -3905443998772158033L;

                    @Override
                    protected void populateItem(ListItem<Map<String, String>> item) {
                        Label name = new Label("code", item.getModelObject().get("code"));
                        item.add(name);

                        Label description = new Label("description", item.getModelObject().get("description"));
                        item.add(description);
                    }
                };
                errors.setVisible(item.getModelObject().getErrors() != null && !item.getModelObject().getErrors().isEmpty());
                body.add(errors);

                Label requestObject = new Label("requestObject", item.getModelObject().getRequestObject());
                requestObject.setVisible(!"".equals(item.getModelObject().getRequestObject()));
                body.add(requestObject);

                Label responseObject = new Label("responseObject", item.getModelObject().getResponseObject());

                Label responseDescription = new Label("responseDescription", item.getModelObject().getResponseDescription());
                if (item.getModelObject().getResponseDescription() == null || "".equals(item.getModelObject().getResponseDescription())) {
                    responseDescription.setVisible(false);
                } else {
                    responseDescription.setVisible(true);
                }
                body.add(responseDescription);

                responseObject.setVisible(!"".equals(item.getModelObject().getResponseObject()));
                body.add(responseObject);
            }
        };
        restAccordion.add(restItems);
    }

}
