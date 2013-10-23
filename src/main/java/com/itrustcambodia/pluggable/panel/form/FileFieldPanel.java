package com.itrustcambodia.pluggable.panel.form;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.form.upload.MultiFileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import com.itrustcambodia.pluggable.core.FormItem;
import com.itrustcambodia.pluggable.panel.InputFeedback;

public class FileFieldPanel extends Panel {

    /**
     * 
     */
    private static final long serialVersionUID = 7570775056642385303L;

    public FileFieldPanel(String id, FormItem<List<FileUpload>> formItem) {
        super(id);
        add(new Label("label", formItem.getLabel()));

        FileUploadField userInput = new FileUploadField("userInput", new PropertyModel<List<FileUpload>>(formItem, "userInput"));
        add(userInput);

        CheckBox delete = new CheckBox("delete", new PropertyModel<Boolean>(formItem, "delete"));
        add(delete);

        InputFeedback feedback = new InputFeedback("feedback", userInput);
        add(feedback);

        HiddenField<String> hiddenField = new HiddenField<String>("inputName", new PropertyModel<String>(formItem, "inputName"));
        add(hiddenField);
    }
}
